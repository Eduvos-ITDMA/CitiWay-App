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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay

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
    val getPlace: suspend (AutocompletePrediction) -> SelectedLocation?,
    val onClearSearch: () -> Unit,
    val onSetSelectedLocation: (SelectedLocation) -> Unit,
    val onUseUserLocation: () -> Unit,
    val getPlaceFromLatLng: suspend (LatLng) -> SelectedLocation
)

class PlacesManager(
    private val application: Application,
    private val apiKey: String,
    private val geocodingService: GeocodingService
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
    * autocompleteSessionToken - Session token for billing - regenerated after fetchPlaces() call
    * placesClient - Main interface for Google Places API calls
    * locationBounds - Rectangular bounds that determine the area within which we accept location predictions

    * Location Bounds Configuration:
    * - Southwest: -34.3°, 18.0° (Cape Point/Simon's Town area)
    * - Northeast: -33.5°, 18.9° (Melkbosstrand/Table View area)
    * - Coverage: ~89km N-S × ~83km E-W (~7,400 km²)
    * - Includes: Entire Cape Town metro area, Peninsula, Northern/Southern suburbs
    * - Uses locationRestriction (not locationBias) to enforce hard boundary limits
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

    /*
    * Debouncing and Job Management:
    * - searchJob: Stores the current search coroutine so we can cancel it when new input arrives
    * - searchDebounceMs: 400ms delay prevents API spam - only searches after user stops typing
    * - SupervisorJob: Isolates coroutine failures so one crashed search doesn't kill the entire scope
    *
    * This prevents the 10k+ API calls/minute issue that occurred when searches triggered on every
    * keystroke and state updates created infinite loops
    */
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var searchJob: Job? = null
    private val searchDebounceMs = 400L

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
        .setMinUpdateIntervalMillis(2000L)
        .setMaxUpdates(1)
        .build()

    // State update functions
    private fun setSelectedLocation(location: SelectedLocation) {
        _state.update { currentState -> currentState.copy(selectedLocation = location) }
    }

    private fun setPredictions(predictions: List<AutocompletePrediction>) {
        _state.update { currentState -> currentState.copy(predictions = predictions) }
    }

    // Updates search text in state only - does NOT trigger API calls automatically
    private fun setSearchText(query: String) {
        _state.update { currentState -> currentState.copy(searchText = query) }
    }

    private fun clearSearch() {
        _state.update { currentState ->
            currentState.copy(searchText = "", predictions = emptyList())
        }
    }

    /**
     * Requests autocomplete predictions from the Places API with debouncing
     *
     * Flow:
     * 1. Cancels any in-flight search to prevent overlapping API calls
     * 2. Updates search text immediately for responsive UI
     * 3. Returns early if query is ≤2 characters (minimum for meaningful results)
     * 4. Waits 400ms after user stops typing before making API call
     * 5. Fetches up to 5 autocomplete predictions within Cape Town bounds
     *
     * This approach reduced API calls from 10k/minute to ~1 per completed search
     */
    private fun searchPlaces(queryText: String) {
        // Cancel any previous search job to prevent concurrent API requests
        searchJob?.cancel()

        // Update the search text immediately (for UI responsiveness)
        setSearchText(queryText)

        // Minimum 2 characters required for autocomplete search
        if (queryText.length <= 2) {
            setPredictions(emptyList())
            return
        }

        // Launch a new search job with debouncing
        searchJob = scope.launch {
            // Wait for the debounce period - if user keeps typing, this job gets cancelled
            delay(searchDebounceMs)

            try {
                // Fetch autocomplete predictions (returns up to 5 results by default)
                val response = placesClient.awaitFindAutocompletePredictions {
                    sessionToken = autocompleteSessionToken
                    locationRestriction = locationBounds
                    query = queryText
                    countries = listOf("ZA")
                }

                setPredictions(response.autocompletePredictions)
            } catch (e: Exception) {
                Log.e("PlacesManager", "Search failed", e)
                setPredictions(emptyList())
            }
        }
    }

    // When user taps on a search suggestion, this function fetches detailed place information and
    // updates the state. It also resets the AutocompleteSessionToken
    private fun selectPlace(prediction: AutocompletePrediction) {
        scope.launch {
            val selectedLocation = getPlace(prediction)
            if (selectedLocation != null) {
                setSearchText(selectedLocation.primaryText)
                setPredictions(emptyList())
                _state.update { currentState ->
                    currentState.copy(
                        selectedLocation = selectedLocation
                    )
                }
            }
        }
    }

    private suspend fun getPlace(prediction: AutocompletePrediction): SelectedLocation? {
        try {
            // Fetch detailed info on the selected location
            val response = placesClient.awaitFetchPlace(prediction.placeId, placeFields) {
                sessionToken = sessionToken
            }

            val place = response.place
            // Reset session token after fetching place details to start new billing session
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

        // Get the Place ID via Reverse Geocoding
        val response = geocodingService.reverseGeocode(latLngString, apiKey)

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

        // Reset session token after fetching place details
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
     * location is disabled in the app via the toggle in the drawer, this function will still use
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

    /**
     * Requests a fresh location update from the device and suspends until received
     *
     * Uses suspendCancellableCoroutine to convert callback-based location API to coroutine-friendly
     * suspend function. Automatically cleans up location updates if coroutine is cancelled.
     */
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
                        // Log and wait for onLocationResult() - FusedLocationProvider will call it once ready
                        Log.w("PlacesManager", "Waiting for temporary location.")
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

    /**
     * Cancels all ongoing operations and cleans up resources
     * Should be called when PlacesManager is no longer needed (e.g., ViewModel.onCleared())
     */
    fun cancel() {
        searchJob?.cancel()
        scope.cancel()
    }
}

data class SelectedLocation(
    val latLng: LatLng, val placeId: String, val primaryText: String
)