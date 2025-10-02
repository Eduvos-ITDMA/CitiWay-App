package com.example.citiway.features.shared

import android.app.Application
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.citiway.BuildConfig
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.kotlin.awaitFetchPlace
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

data class LocationSelectionState(
    val searchText: String = "",
    val selectedLocation: LatLng? = null,
    val userLocation: LatLng? = null,
    val predictions: List<AutocompletePrediction> = emptyList(),
    val showPredictions: Boolean = false,
    val isLocationPermissionGranted: Boolean = false
)

data class LocationSelectionActions(
    val setSelectedLocation: (LatLng?) -> Unit,
    val setSearchText: (String) -> Unit,
    val setUserLocation: (LatLng?) -> Unit,
    val toggleShowPredictions: (Boolean) -> Unit,
    val searchPlaces: (String) -> Unit,
    val selectPlace: (AutocompletePrediction) -> Unit,
    val reverseGeocode: (LatLng) -> Unit,
    val getCurrentLocation: () -> Unit,
    val onLocationPermissionsStatusChanged: (Boolean) -> Unit
)

object DefaultLocations {
    val CAPE_TOWN = LatLng(BuildConfig.CAPE_TOWN_LAT, BuildConfig.CAPE_TOWN_LNG)
    val SOUTHWEST_BOUND =
        LatLng(BuildConfig.SOUTHWEST_CAPE_TOWN_LAT, BuildConfig.SOUTHWEST_CAPE_TOWN_LNG)
    val NORTHEAST_BOUND =
        LatLng(BuildConfig.NORTHEAST_CAPE_TOWN_LAT, BuildConfig.NORTHEAST_CAPE_TOWN_LNG)
}

class LocationSelectionViewModel(application: Application) : AndroidViewModel(application) {
    /*
    * TODO: Track if location is enabled. If turned off, functions will have to behave differently
    */
    private val _screenState = MutableStateFlow(LocationSelectionState())
    val screenState: StateFlow<LocationSelectionState> = _screenState

    val actions = LocationSelectionActions(
        this::setSelectedLocation,
        this::updateSearchText,
        this::setUserLocation,
        this::toggleShowPredictions,
        this::searchPlaces,
        this::selectPlace,
        this::reverseGeocode,
        this::getCurrentLocation,
        this::onLocationPermissionStatusChanged
    )

    /*
    * autocompleteSessionToken - session token for billing - regenerated after fetchPlaces() call
    * placesClient - Main interface for Google Places API calls
    * geocoder - Android's built-in service for converting coordinates to addresses
    * cameraPositionState - controls what the user sees on the map
    */
    private var autocompleteSessionToken = AutocompleteSessionToken.newInstance()
    private val placesClient: PlacesClient = Places.createClient(application)
    private val geocoder = Geocoder(application, Locale.getDefault())
    val cameraPositionState =
        CameraPositionState(CameraPosition.fromLatLngZoom(DefaultLocations.CAPE_TOWN, 12f))
    private val scope = CoroutineScope(Dispatchers.Main)

    fun updateSearchText(text: String) {
        _screenState.update { currentState ->
            currentState.copy(searchText = text)
        }
    }

    fun setSelectedLocation(location: LatLng?) {
        _screenState.update { currentState ->
            currentState.copy(selectedLocation = location)
        }
    }

    fun setUserLocation(location: LatLng?) {
        _screenState.update { currentState ->
            currentState.copy(userLocation = location)
        }
    }

    fun toggleShowPredictions(show: Boolean) {
        _screenState.update { currentState ->
            currentState.copy(showPredictions = show)
        }
    }

    fun setPredictions(predictions: List<AutocompletePrediction> = emptyList()) {
        _screenState.update { currentState ->
            currentState.copy(showPredictions = predictions.isEmpty(), predictions = predictions)
        }
    }

    fun onLocationPermissionStatusChanged(isGranted: Boolean) {
        _screenState.update { currentState ->
            currentState.copy(isLocationPermissionGranted = isGranted)
        }
    }

    /*
     * This function handles real-time search as the user types.
     * Key features:
     * 1. Only searches after 2+ characters to avoid too many API calls
     * 2. LocationBias restricts results to Cape Town area for relevance
     * 3. setCountries("ZA") ensures only South African locations appear
     * 4. Automatically shows/hides dropdown based on results
     */
    fun searchPlaces(queryText: String) {
        if (queryText.length > 2) {
            viewModelScope.launch {
                val bounds = RectangularBounds.newInstance(
                    DefaultLocations.SOUTHWEST_BOUND, DefaultLocations.NORTHEAST_BOUND
                )

                try {
                    val response = placesClient.awaitFindAutocompletePredictions {
                        sessionToken = autocompleteSessionToken
                        locationBias = bounds
                        query = query
                        countries = listOf("ZA")
                    }

                    setPredictions(response.autocompletePredictions)
                } catch (e: Exception) {
                    setPredictions()
                }
            }
        }
    }

    /*
     * When user taps on a search suggestion, this function:
     * 1. Fetches detailed place information including coordinates
     * 2. Updates the search text with a clean location name
     * 3. Sets the marker position on the map
     * 4. Moves camera to the selected location
     * 5. Hides the suggestions dropdown
     */
    fun selectPlace(prediction: AutocompletePrediction) {
        viewModelScope.launch {
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.DISPLAY_NAME,
                Place.Field.LOCATION,
                Place.Field.FORMATTED_ADDRESS
            )

            try {
                // Fetch detailed info on the selected location
                val response = placesClient.awaitFetchPlace(prediction.placeId, placeFields) {
                    sessionToken = sessionToken
                }

                val place = response.place

                // Update state with place info
                place.location?.let { latLng ->
                    _screenState.update { currentState ->
                        currentState.copy(
                            selectedLocation = latLng,
                            searchText = place.displayName ?: prediction.getPrimaryText(null)
                                .toString(),
                            showPredictions = false
                        )
                    }

                    // Adjust map camera
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            } catch (e: Exception) {
                // TODO: Handle fetchPlace failure
            }
        }
    }

    /*
    * Reverse geocode to get address from LatLng
    * If geocoding fails, we fall back to a generic "Selected Location" text.
    */
    fun reverseGeocode(latLng: LatLng) {
        scope.launch {
            try {
                // requires API level 31 (TIRAMISU)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                        val address = addresses.firstOrNull()
                        if (address != null) {
                            updateSearchText(address.getAddressLine(0) ?: "Selected Location")
                        } else {
                            updateSearchText("Selected Location")
                        }
                    }
                } else {
                    // fallback for older APIs
                    @Suppress("DEPRECATION")
                    // Prevent blocking main thread with old getFromLocation() method
                    val addresses = withContext(Dispatchers.IO) {
                        geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    }
                    if (addresses?.isNotEmpty() == true) {
                        val address = addresses[0]
                        updateSearchText(address.getAddressLine(0) ?: "Selected Location")
                    } else {
                        updateSearchText("Selected Location")
                    }
                }
            } catch (e: Exception) {
                updateSearchText("Selected Location")
            }
        }
    }

    // Get current user location
    fun getCurrentLocation() {
        // TODO: Use Hilt to inject fusedLocationClient in the future
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

        scope.launch {
            try {/*
             * Location Retrieval Process:
             * 1. lastLocation gives us cached location (faster than real-time GPS)
             * 2. addOnSuccessListener handles async response when location is found
             * 3. We convert Android Location to Google Maps LatLng format
             * 4. Update both userLocation (for tracking) and selectedLocation (for UI)
             * 5. Reverse geocode to show user their current address
             * 6. Move camera to user's position with 15f zoom (street level view)
             */
                if (!_screenState.value.isLocationPermissionGranted) {
                    return@launch
                }

                val location = fusedLocationClient.lastLocation.await()
                val latLng = LatLng(location.latitude, location.longitude)
                _screenState.update { currentState ->
                    currentState.copy(userLocation = latLng, selectedLocation = latLng)
                }

                reverseGeocode(latLng)
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                )
            } catch (e: SecurityException) {
                // TODO: Handle permission issues
            }
        }
    }
}
