package com.example.citiway.data.remote

import android.app.Application
import android.location.Geocoder
import com.example.citiway.BuildConfig
import com.google.android.gms.location.LocationServices
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class PlacesManager(
    private val application: Application,
    private val geocodingService: GeocodingService
) {
    /*
    * autocompleteSessionToken - session token for billing - regenerated after fetchPlaces() call
    * placesClient - Main interface for Google Places API calls
    * geocoder - Android's built-in service for converting coordinates to addresses
    * locationBounds - Rectangular bounds tht determines the area within which we accept location predictions
    */
    private var autocompleteSessionToken = AutocompleteSessionToken.newInstance()
    private val placesClient: PlacesClient = Places.createClient(application)
    private val geocoder = Geocoder(application, Locale.getDefault())
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

    // Expose state flows
    private val _selectedLocation = MutableStateFlow<SelectedLocation?>(null)
    val selectedLocation: StateFlow<SelectedLocation?> = _selectedLocation
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation
    private val _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictions: MutableStateFlow<List<AutocompletePrediction>> = _predictions
    private val _searchText = MutableStateFlow("")
    val searchText: MutableStateFlow<String> = _searchText

    fun setSelectedLocation(location: SelectedLocation) {
        _selectedLocation.value = location
    }

    fun setSearchText(query: String) {
        _searchText.value = query
    }

    fun clearSearch() {
        _searchText.value = ""
        _predictions.value = emptyList()
    }

    // This function requests a list of predictions from the Places API and updates the _predictions field
    fun searchPlaces(queryText: String) {
        if (queryText.length > 2) {
            scope.launch {
                try {
                    val response = placesClient.awaitFindAutocompletePredictions {
                        sessionToken = autocompleteSessionToken
                        locationBias = locationBounds
                        query = queryText
                        countries = listOf("ZA")
                    }

                    _predictions.value = response.autocompletePredictions
                } catch (e: Exception) {
                    _predictions.value = emptyList()
                }
            }
        }
    }

    // When user taps on a search suggestion, this function fetches detailed place information and
    // updates the search text with a clean location name. It also resets the AutocompleteSessionToken
    fun selectPlace(prediction: AutocompletePrediction) {
        scope.launch {
            try {
                // Fetch detailed info on the selected location
                val response = placesClient.awaitFetchPlace(prediction.placeId, placeFields) {
                    sessionToken = sessionToken
                }

                val place = response.place
                autocompleteSessionToken = AutocompleteSessionToken.newInstance()

                // Update state with place info
                place.location?.let { latLng ->
                    val primaryText =
                        place.displayName ?: prediction.getPrimaryText(null).toString()
                    _selectedLocation.value = SelectedLocation(latLng, place.id ?: "", primaryText)
                    _searchText.value = primaryText
                }
            } catch (e: Exception) {
                // TODO: Handle fetchPlace failure
            }
        }
    }

    // This function uses the GeocodingService retrofit API and Places API to convert
    // a LatLng value to a SelectedLocation
    suspend fun getPlaceFromLatLng(latLng: LatLng): SelectedLocation {
        val latLngString = "${latLng.latitude},${latLng.longitude}"

        // Get the Place ID via Reverse Geocoding (HTTP call)
        val response = geocodingService.reverseGeocode(latLngString, BuildConfig.MAPS_API_KEY)

        if (response.status != "OK" || response.results.isEmpty()) {
            throw Exception("Reverse Geocoding failed: ${response.status}")
        }

        // Use the first result's Place ID
        val placeId =
            response.results.first().placeId
                ?: throw Exception("Place ID not found in response.")

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
    fun useUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

        scope.launch {
            try {
                val location = fusedLocationClient.lastLocation.await()
                val latLng = LatLng(location.latitude, location.longitude)

                val place = getPlaceFromLatLng(latLng)
                _userLocation.value = latLng
                _selectedLocation.value = place
            } catch (e: SecurityException) {
                throw SecurityException(
                    "Location permission not granted - user location cannot be used",
                    e
                )
            }
        }
    }
}

data class SelectedLocation(
    val latLng: LatLng,
    val placeId: String,
    val primaryText: String
)