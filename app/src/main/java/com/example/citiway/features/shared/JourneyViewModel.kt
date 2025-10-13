package com.example.citiway.features.shared

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.toSecondsInt
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.data.remote.Step
import com.example.citiway.data.remote.Vehicle
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Locale.getDefault

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
                val routes = routesManager.getTransitRoutes(start, destination)

                val journeyOptions = routes.map { route ->
                    val steps = route.legs.firstOrNull()?.steps ?: emptyList()

                    fun getVehicle(step: Step?): Vehicle? {
                        return step?.transitDetails?.transitLine?.vehicle
                    }

                    // firstWalkDuration: first WALK step's duration
                    val firstWalkDurationMinutes =
                        getNextDepartureDuration(steps)?.toMinutes()?.toInt()

                    // firstNodeType: find first TRANSIT step and decide STOP vs STATION
                    val firstTransitStep = steps.firstOrNull { it.travelMode == "TRANSIT" }
                    val firstNodeType = getVehicle(firstTransitStep)?.type?.let { vehicleType ->
                        when (vehicleType.uppercase()) {
                            "HEAVY_RAIL", "RAIL", "SUBWAY", "COMMUTER_RAIL" -> TravelPoint.STATION
                            else -> TravelPoint.STOP
                        }
                    } ?: TravelPoint.STOP

                    // routeSegments: iterate through steps to build string segments
                    val segments = mutableListOf<String>()
                    steps.forEach { step ->
                        when (step.travelMode) {
                            "WALK" -> segments.add("Walk")
                            "TRANSIT" -> {
                                val vehicleType = getVehicle(step)?.type?.uppercase()
                                when (vehicleType) {
                                    "BUS" -> {
                                        segments.add("MyCiTi")
                                    }

                                    "HEAVY_RAIL", "RAIL" -> {
                                        segments.add("Metrorail")
                                    }

                                    else -> {
                                        // Fallback for flexibility
                                        val vehicleName = getVehicle(step)?.name?.text
                                        segments.add(vehicleName ?: "Transit")
                                    }
                                }
                            }

                            else -> {
                                // Fallback for flexibility - just map generic name
                                segments.add(step.travelMode.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(getDefault())
                                    else it.toString()
                                })
                            }
                        }
                    }

                    // nextDeparture: time until next departure
                    val nextDeparture = Duration.between(
                        Instant.now(),
                        Instant.parse(firstTransitStep?.transitDetails?.stopDetails?.departureTime)
                    )

                    // arrivalTime: current time + route.duration.value
                    val arrivalTime = calculateArrivalTime(steps)

                    // TODO: fareTotal
                    var fare = 0.0f
                    steps.forEach { step ->
                        if (step.travelMode == "TRANSIT") {
                            when (getVehicle(step)?.type?.uppercase()) {
                                "BUS" -> fare += 200f
                                "HEAVY_RAIL", "RAIL" -> fare += 10f
                                else -> fare = 0f
                            }
                        }
                    }

                    JourneyDetails(
                        firstWalkDuration = firstWalkDurationMinutes,
                        firstNodeType = firstNodeType,
                        routeSegments = segments,
                        nextDeparture = nextDeparture,
                        arrivalTime = arrivalTime,
                        fareTotal = fare
                    )
                }

                Log.d("Route update journeyOptions", journeyOptions.toString())
                _state.update { it.copy(journeyOptions = journeyOptions) }
            }
        }
    }

    fun getNextDepartureDuration(steps: List<Step>): Duration? {
        val now = Instant.now()

        steps.forEach { step ->
            if (step.travelMode == "TRANSIT") {
                val departureTimeString = step.transitDetails?.stopDetails?.departureTime

                if (departureTimeString != null) {
                    return try {
                        Duration.between(now, Instant.parse(departureTimeString))
                    } catch (e: DateTimeParseException) {
                        println("Error parsing departure time string: $departureTimeString. Error: ${e.message}")
                        null
                    }
                }
            }
        }

        return null
    }

    fun calculateArrivalTime(steps: List<Step>): Instant? {
        var walkDuration = 0
        steps.reversed().forEach { step ->
            when (step.travelMode) {
                "WALK" -> walkDuration += step.staticDuration.toSecondsInt()
                "TRANSIT" -> return Instant.parse(step.transitDetails?.stopDetails?.arrivalTime)
                    ?.plusSeconds(walkDuration.toLong())

                else -> return null
            }
        }

        return null
    }

}

data class JourneyState(
    var startLocation: SelectedLocation? = null,
    var destination: SelectedLocation? = null,
    var journeyOptions: List<JourneyDetails> = emptyList()
)

data class JourneyDetails(
    val firstWalkDuration: Int?,
    val firstNodeType: TravelPoint?,
    val routeSegments: List<String>?,
    val nextDeparture: Duration?,
    val arrivalTime: Instant?,
    val fareTotal: Float = 0f,
)

enum class TravelPoint(val label: String) {
    STOP("stop"), STATION("station")
}

enum class LocationType {
    START, END
}