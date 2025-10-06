package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.BuildConfig
import com.example.citiway.data.remote.PlacesManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapState(
    val searchText: String = "",
    val selectedLocation: LatLng? = null,
    val userLocation: LatLng? = null,
    val isLocationPermissionGranted: Boolean = false
)

data class MapActions(
    val selectLocationOnMap: (LatLng) -> Unit
)

class MapViewModel(
    private val placesManager: PlacesManager,
    private val locationType: LocationType = LocationType.START
) : ViewModel() {
    private val _screenState = MutableStateFlow(MapState())
    val screenState: StateFlow<MapState> = _screenState

    val actions = MapActions(
        this::selectLocationOnMap
    )

    val cameraPositionState =
        CameraPositionState(
            CameraPosition.fromLatLngZoom(
                LatLng(
                    BuildConfig.CAPE_TOWN_LAT,
                    BuildConfig.CAPE_TOWN_LNG
                ), 12f
            )
        )

    init {
        viewModelScope.launch {
            placesManager.selectedLocation.collect { location ->
                _screenState.update { currentState ->
                    currentState.copy(selectedLocation = location)
                }

                if (location != null) {
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }
            }

            placesManager.userLocation.collect { location ->
                _screenState.update { currentState ->
                    currentState.copy(userLocation = location)
                }
            }
        }
    }

    fun onLocationPermissionsStatusChanged(isGranted: Boolean){
        _screenState.update { it.copy(isLocationPermissionGranted = isGranted) }
    }

    fun selectLocationOnMap(location: LatLng) {
        placesManager.setSelectedLocation(location)
        placesManager.reverseGeocode(location)
        // TODO: Notify JourneyViewModel of selected location and pass in hostScreen
    }
}