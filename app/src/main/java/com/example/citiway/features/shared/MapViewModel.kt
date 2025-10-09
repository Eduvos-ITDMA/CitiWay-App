package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.App
import com.example.citiway.BuildConfig
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.data.remote.SelectedLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.compose
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapState(
    val searchText: String = "",
    val selectedLocation: SelectedLocation? = null,
    val userLocation: LatLng? = null,
    val isLocationPermissionGranted: Boolean = false
)

data class MapActions(
    val selectLocationOnMap: (LatLng) -> Unit
)

class MapViewModel(
    private val placesState: StateFlow<PlacesState> = App.appModule.placesManager.state,
    val placesActions: PlacesActions = App.appModule.placesManager.actions
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
            placesState.collect { placesState ->
                _screenState.update { currentState ->
                    currentState.copy(
                        selectedLocation = placesState.selectedLocation,
                        userLocation = placesState.userLocation
                    )
                }

                val location = placesState.selectedLocation
                if (location != null) {
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(
                            location.latLng,
                            15f
                        )
                    )
                }
            }
        }
    }

    fun clearSearch() {
        _screenState.update { it.copy(searchText = "") }
        placesActions.onClearSearch()
    }

    fun selectLocationOnMap(location: LatLng) {
        viewModelScope.launch {
            val selectedLocation = placesActions.getPlaceFromLatLng(location)
            placesActions.onSetSelectedLocation(selectedLocation)
            placesActions.onSetSearchText(selectedLocation.primaryText)
        }
    }
}