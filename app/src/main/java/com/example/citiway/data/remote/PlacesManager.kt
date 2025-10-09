package com.example.citiway.data.remote

import android.app.Application
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Stable
import com.example.citiway.BuildConfig
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.kotlin.awaitFetchPlace
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resumeWithException

@Stable
data class PlacesState(
    val selectedLocation: SelectedLocation? = null,
    val userLocation: LatLng? = null,
    val predictions: List<AutocompletePrediction> = emptyList(),
    val searchText: String = ""
)

class PlacesActions(
    val onSetSearchText: (String) -> Unit,
    val onSearchPlaces: (String) -> Unit,
    val onSelectPlace: (AutocompletePrediction) -> Unit,
    val getPlace: suspend (AutocompletePrediction) -> SelectedLocation,
    val onClearSearch: () -> Unit,
    val onSetSelectedLocation: (SelectedLocation) -> Unit,
    val onUseUserLocation: () -> Unit,
    val getPlaceFromLatLng: suspend (LatLng) -> SelectedLocation
)

class PlacesManager(
    private val application: Application, private val geocodingService: GeocodingService
) {
    private val _state = MutableStateFlow(PlacesState())
    val state: StateFlow<PlacesState> = _state

    val actions = PlacesActions(
        onSetSearchText = ::setSearchText,
        onSearchPlaces = ::searchPlaces,
        onSelectPlace = ::selectPlace,
        getPlace = ::getPlace,
        onClearSearch = ::clearSearch,
        onSetSelectedLocation = ::setSelectedLocation,
        onUseUserLocation = ::useUserLocation,
        getPlaceFromLatLng = ::getPlaceFromLatLng
    )

    /*
    * autocompleteSessionToken - session token for billing - regenerated after fetchPlaces() call
    * placesClient - Main interface for Google Places API calls
    * geocoder - Android's built-in service for converting coordinates to addresses
    * locationBounds - Rectangular bounds tht determines the area within which we accept location predictions
    */
    private var autocompleteSessionToken = AutocompleteSessionToken.newInstance()
    private val placesClient: PlacesClient = Places.createClient(application)
    private val locationBounds = RectangularBounds.newInstance(
        LatLng(BuildConfig.SOUTHWEST_CAPE_TOWN_LAT, BuildConfig.SOUTHWEST_CAPE_TOWN_LNG),
        LatLng(BuildConfig.NORTHEAST_CAPE_TOWN_LAT, BuildConfig.NORTHEAST_CAPE_TOWN_LNG)
    )
    private val placeFields = listOf(
        Place.Field.ID,
        Place.Field.DISPLAY_NAME,
        Place.Field.LOCATION,
        Place.Field.FORMATTED_ADDRESS
    )
    private val scope = CoroutineScope(Dispatchers.Main)
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
        .setMinUpdateIntervalMillis(2000L)
        .setMaxUpdates(1)
        .build()

    // Expose state flows
    private fun setSelectedLocation(location: SelectedLocation) {
        _state.update { currentState -> currentState.copy(selectedLocation = location) }
    }

    private fun setPredictions(predictions: List<AutocompletePrediction>) {
        _state.update { currentState -> currentState.copy(predictions = predictions) }
    }

    private fun setSearchText(query: String) {
        Log.d("places setSearchText", query)
        _state.update { currentState -> currentState.copy(searchText = query) }
    }

    private fun clearSearch() {
        Log.d("places clear search", "Search cleared")
        _state.update { currentState ->
            currentState.copy(searchText = "", predictions = emptyList())
        }
    }

    // This function requests a list of predictions from the Places API and updates the state
    private fun searchPlaces(queryText: String) {
        setSearchText(queryText)
        if (queryText.length <= 2) {
            setPredictions(emptyList())
        } else {
            scope.launch {
                try {
                    val response = placesClient.awaitFindAutocompletePredictions {
                        sessionToken = autocompleteSessionToken
                        locationBias = locationBounds
                        query = queryText
                        countries = listOf("ZA")
                    }

                    _state.update { currentState -> currentState.copy(predictions = response.autocompletePredictions) }
                } catch (e: Exception) {
                    _state.update { currentState -> currentState.copy(predictions = emptyList()) }
                }
            }
        }
    }

    // When user taps on a search suggestion, this function fetches detailed place information and
    // updates the state. It also resets the AutocompleteSessionToken
    private fun selectPlace(prediction: AutocompletePrediction) {
        scope.launch {
            val selectedLocation = getPlace(prediction)
            setSearchText(selectedLocation.primaryText)
            setPredictions(emptyList())
            _state.update { currentState ->
                currentState.copy(
                    selectedLocation = selectedLocation, userLocation = selectedLocation.latLng
                )
            }
        }
    }

    private suspend fun getPlace(prediction: AutocompletePrediction): SelectedLocation {
        try {
            // Fetch detailed info on the selected location
            val response = placesClient.awaitFetchPlace(prediction.placeId, placeFields) {
                sessionToken = sessionToken
            }

            val place = response.place
            autocompleteSessionToken = AutocompleteSessionToken.newInstance()

            val latLng = place.location ?: throw Exception("Place location is null.")
            val primaryText = place.displayName ?: prediction.getPrimaryText(null).toString()

            return SelectedLocation(latLng, place.id ?: "", primaryText)
        } catch (e: Exception) {
            throw Exception("Places API failed to fetch place", e)
        }
    }

    // This function uses the GeocodingService retrofit API and Places API to convert
    // a LatLng value to a SelectedLocation
    private suspend fun getPlaceFromLatLng(latLng: LatLng): SelectedLocation {
        val latLngString = "${latLng.latitude},${latLng.longitude}"

        // Get the Place ID via Reverse Geocoding (HTTP call)
        val response = geocodingService.reverseGeocode(latLngString, BuildConfig.MAPS_API_KEY)

        if (response.status != "OK" || response.results.isEmpty()) {
            throw Exception("Reverse Geocoding failed: ${response.status}")
        }

        // Use the first result's Place ID
        val placeId =
            response.results.first().placeId ?: throw Exception("Place ID not found in response.")

        // Fetch the full Place object using the native PlacesClient
        val place = placesClient.awaitFetchPlace(placeId, placeFields) {
            sessionToken = sessionToken
        }.place

        autocompleteSessionToken = AutocompleteSessionToken.newInstance()

        return SelectedLocation(
            latLng = latLng,
            placeId = place.id ?: placeId,
            primaryText = place.displayName ?: place.formattedAddress ?: "Map Click Location"
        )
    }

    /**
     * Sets the selectedLocation property to the device's current location using the
     * fusedLocationClient
     *
     * IMPORTANT: Throws exception if location permission is not granted by the user. Even if
     * location is disabled in the app via the toggle in the drawer, this function will still user
     * the device's location. Therefore the responsibility is on the caller to check for location
     * permission granted AND location is enabled in the app
     */
    private fun useUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

        scope.launch {
            try {
                val location = getFreshLocation(fusedLocationClient)
                val latLng = LatLng(location.latitude, location.longitude)

                val place = getPlaceFromLatLng(latLng)
                _state.update { currentState ->
                    currentState.copy(
                        userLocation = latLng, selectedLocation = place
                    )
                }
            } catch (e: SecurityException) {
                throw SecurityException(
                    "Location permission not granted - user location cannot be used", e
                )
            }
        }
    }

    private suspend fun getFreshLocation(fusedLocationClient: FusedLocationProviderClient): Location =
        suspendCancellableCoroutine { continuation ->

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // Get the latest location
                    val location = locationResult.lastLocation

                    fusedLocationClient.removeLocationUpdates(this)

                    // Resume the coroutine with the result
                    if (location != null) {
                        continuation.resume(location) { cause, _, _ -> }
                    } else {
                        continuation.resumeWithException(
                            IllegalStateException("Location result received, but location object was null.")
                        )
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    if (!locationAvailability.isLocationAvailable) {
                        // Handle cases where the GPS/system isn't ready
                        continuation.resumeWithException(
                            IllegalStateException("Location services are unavailable on the device.")
                        )
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }

            // Request the location update
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                continuation.resumeWithException(e)
            }

            // Stop location updates if coroutine is cancelled
            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        }
}

data class SelectedLocation(
    val latLng: LatLng, val placeId: String, val primaryText: String
)