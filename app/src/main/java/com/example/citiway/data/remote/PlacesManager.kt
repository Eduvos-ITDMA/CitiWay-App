package com.example.citiway.data.remote

import android.app.Application
import android.location.Geocoder
import android.util.Log
import com.example.citiway.BuildConfig
import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.data.local.RecentSearch
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

    // ✨ ADD THIS: Initialize the database
    private val database = CitiWayDatabase.getDatabase(application)

    // Expose state flows
    private val _selectedLocation = MutableStateFlow<SelectedLocation?>(null)
    val selectedLocation: StateFlow<SelectedLocation?> = _selectedLocation
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation
    private val _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictions: MutableStateFlow<List<AutocompletePrediction>> = _predictions
    private val _searchText = MutableStateFlow("")
    val searchText: MutableStateFlow<String> = _searchText

    // ✨ ADD THIS: Expose recent searches from database
    private val _recentSearches = MutableStateFlow<List<RecentSearch>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches

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

    // ✨ ADD THIS: Load recent searches from database
    fun loadRecentSearches() {
        scope.launch(Dispatchers.IO) {
            val searches = database.recentSearchDao().getRecentSearches()
            _recentSearches.value = searches
            Log.d("PlacesManager", "📖 Loaded ${searches.size} recent searches from database")
        }
    }

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

    // ✨ MODIFIED: Now saves to database when user selects a place
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
                    val secondaryText = place.formattedAddress ?: prediction.getSecondaryText(null).toString()

                    _selectedLocation.value = SelectedLocation(latLng, place.id ?: "", primaryText)
                    _searchText.value = primaryText

                    // ✨ SAVE TO DATABASE!
                    saveToDatabase(
                        placeId = place.id ?: "",
                        placeName = primaryText,
                        placeAddress = secondaryText,
                        latLng = latLng
                    )
                }
            } catch (e: Exception) {
                Log.e("PlacesManager", "Failed to select place", e)
            }
        }
    }

    // ✨ ADD THIS: Helper function to save searches
    private fun saveToDatabase(
        placeId: String,
        placeName: String,
        placeAddress: String,
        latLng: LatLng
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                val search = RecentSearch(
                    placeId = placeId,
                    placeName = placeName,
                    placeAddress = placeAddress,
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                )
                database.recentSearchDao().insertSearch(search)
                Log.d("PlacesManager", "✅ Saved to database: $placeName")

                // Reload the list
                loadRecentSearches()
            } catch (e: Exception) {
                Log.e("PlacesManager", "❌ Failed to save to database", e)
            }
        }
    }

    suspend fun getPlaceFromLatLng(latLng: LatLng): SelectedLocation {
        val latLngString = "${latLng.latitude},${latLng.longitude}"

        val response = geocodingService.reverseGeocode(latLngString, BuildConfig.MAPS_API_KEY)

        if (response.status != "OK" || response.results.isEmpty()) {
            throw Exception("Reverse Geocoding failed: ${response.status}")
        }

        val placeId =
            response.results.first().placeId
                ?: throw Exception("Place ID not found in response.")

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