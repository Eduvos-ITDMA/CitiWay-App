package com.example.citiway.features.start_location_selection

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StartLocationSelectionViewModel(application: Application) : AndroidViewModel(application) {

    // StateFlow variables
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    private val _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    /** List of autocomplete suggestions from Google Places API */
    val predictions: StateFlow<List<AutocompletePrediction>> = _predictions

    private val _showPredictions = MutableStateFlow(false)
    /** Controls visibility of the dropdown suggestion list */
    val showPredictions: StateFlow<Boolean> = _showPredictions

    private val scope = CoroutineScope(Dispatchers.Main)

    private val placesClient: PlacesClient = Places.createClient(context)
    private val geocoder = Geocoder(context, Locale.getDefault())

    // Functions to update flows
    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun setSelectedLocation(latLng: LatLng?) {
        _selectedLocation.value = latLng
    }

    fun setUserLocation(latLng: LatLng?) {
        _userLocation.value = latLng
    }

    fun toggleShowPredictions(show: Boolean) {
        _showPredictions.value = show
    }

    // Search places based on query
    fun searchPlaces(query: String) {
        if (query.length > 2) {
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .setLocationBias(
                    RectangularBounds.newInstance(
                        LatLng(-34.3, 18.0),
                        LatLng(-33.5, 18.9)
                    )
                )
                .setCountries("ZA")
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    _predictions.value = response.autocompletePredictions
                    _showPredictions.value = true
                }
                .addOnFailureListener {
                    _predictions.value = emptyList()
                    _showPredictions.value = false
                }
        } else {
            _predictions.value = emptyList()
            _showPredictions.value = false
        }
    }

    // Select place from predictions
    fun selectPlace(prediction: AutocompletePrediction) {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request = FetchPlaceRequest.newInstance(prediction.placeId, placeFields)

        scope.launch {
            try {
                val response = placesClient.fetchPlace(request).await()
                val place = response.place
                place.latLng?.let { latLng ->
                    _selectedLocation.value = latLng
                    _searchText.value = place.name ?: prediction.getPrimaryText(null).toString()
                    toggleShowPredictions(false)
                    // Assume that camera movement is handled via callback or separate state
                }
            } catch (e: Exception) {
                // Handle failure if needed
            }
        }
    }

    // Reverse geocode to get address from LatLng
    fun reverseGeocode(latLng: LatLng) {
        scope.launch {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    _searchText.value = address.getAddressLine(0) ?: "Selected Location"
                }
            } catch (e: Exception) {
                _searchText.value = "Selected Location"
            }
        }
    }

    // Get current user location
    fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        scope.launch {
            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    _userLocation.value = latLng
                    _selectedLocation.value = latLng
                    reverseGeocode(latLng)
                    // Camera move can be handled separately
                }
            } catch (e: SecurityException) {
                // Handle permission issues
            }
        }
    }
}