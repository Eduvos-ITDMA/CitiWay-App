package com.example.citiway.data.remote

import android.app.Application
import android.location.Geocoder
import android.os.Build
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
import kotlinx.coroutines.withContext
import java.util.Locale

class PlacesManager(private val application: Application) {
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
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation
    private val _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictions: MutableStateFlow<List<AutocompletePrediction>> = _predictions
    private val _searchText = MutableStateFlow<String>("")
    val searchText: MutableStateFlow<String> = _searchText

    fun setSelectedLocation(location: LatLng) {
        _selectedLocation.value = location
    }

    fun setSearchText(query: String) {
        _searchText.value = query
    }

    fun setUserLocation(location: LatLng) {
        _userLocation.value = location
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

    /*
     * When user taps on a search suggestion, this function:
     * 1. Fetches detailed place information including coordinates
     * 2. Updates the search text with a clean location name
     * 3. Sets the marker position on the map
     * 4. Moves camera to the selected location
     * 5. Hides the suggestions dropdown
     */
    fun selectPlace(prediction: AutocompletePrediction) {
        scope.launch {
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
                autocompleteSessionToken = AutocompleteSessionToken.newInstance()

                // Update state with place info
                place.location?.let { latLng ->
                    _selectedLocation.value = latLng
                    _searchText.value = place.displayName ?: prediction.getPrimaryText(null)
                        .toString()
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
                            setSearchText((address.getAddressLine(0) ?: "Selected Location"))
                        } else {
                            setSearchText("Selected Location")
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
                        setSearchText(address.getAddressLine(0) ?: "Selected Location")
                    } else {
                        setSearchText("Selected Location")
                    }
                }
            } catch (e: Exception) {
                setSearchText("Selected Location")
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
                // DUAL CHECK: Both system permission AND app preference must be enabled
                if (!currentState.isLocationPermissionGranted || !currentState.isAppLocationEnabled) {
                    return@launch
                }

                val location = fusedLocationClient.lastLocation.await()
                val latLng = LatLng(location.latitude, location.longitude)
                _userLocation.value = latLng
                _selectedLocation.value = latLng
            } catch (e: SecurityException) {
                // TODO: Handle permission issues
            }
        }
    }
}