package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.SelectedLocation
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class JourneyViewModel(
    private val navController: NavController,
    private val routesManager: RoutesManager = App.appModule.routesManager
) : ViewModel() {
    private val _state = MutableStateFlow(JourneyState())
    val state: StateFlow<JourneyState> = _state

    init {
        viewModelScope.launch {}
    }

    fun confirmLocationSelection(
        selectedLocation: SelectedLocation, locationType: LocationType, clearSearch: () -> Unit
    ) {
        when (locationType) {
            LocationType.START -> {
                _state.update { it.copy(startLocation = selectedLocation) }
                clearSearch()
                navController.navigate(Screen.JourneySelection.route)
            }

            LocationType.END -> {
                _state.update { it.copy(destination = selectedLocation) }
                clearSearch()
                navController.navigate(Screen.StartLocationSelection.route)
            }
        }
    }

    fun setJourneyOptions() {
        val start: LatLng? = state.value.startLocation?.latLng
        val destination: LatLng? = state.value.destination?.latLng
        if (start != null && destination != null) {

            viewModelScope.launch {
                // val routes = routesManager.getTransitRoutes(start, destination)
                // TODO: Calculate fare total for each route

                _state.update {
                    it.copy(
                        journeyOptions = listOf(
                            JourneyDetails(
                                10,
                                TravelPoint.STOP,
                                listOf("Walk", "MyCiTi", "Walk", "Metrorail", "Walk"),
                                15,
                                LocalTime.of(8, 30)
                            ), JourneyDetails(
                                8,
                                TravelPoint.STATION,
                                listOf("Walk", "Metrorail", "Walk", "MyCiTi", "Walk"),
                                20,
                                LocalTime.of(15, 45)
                            )
                        )
                    )
                }
            }
        }
    }
}

data class JourneyState(
    var startLocation: SelectedLocation? = null,
    var destination: SelectedLocation? = null,
    var journeyOptions: List<JourneyDetails> = emptyList()
)

data class JourneyDetails(
    val firstWalkDuration: Int,
    val firstNodeType: TravelPoint,
    val routeSegments: List<String>,
    val nextDeparture: Int,
    val arrivalTime: LocalTime,
    val fareTotal: Float = 0f,
)

enum class TravelPoint(val label: String) {
    STOP("stop"), STATION("station")
}

enum class LocationType {
    START, END
}