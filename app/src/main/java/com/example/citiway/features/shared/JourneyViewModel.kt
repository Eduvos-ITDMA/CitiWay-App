package com.example.citiway.features.shared

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.HOURS_MINUTES_FORMATTER
import com.example.citiway.core.utils.convertHourToInstantIso
import com.example.citiway.core.utils.convertIsoToHhmm
import com.example.citiway.core.utils.getNearestHalfHour
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale.getDefault
import kotlin.math.ceil

class JourneyViewModel(
    private val navController: NavController,
    private val routesManager: RoutesManager = App.appModule.routesManager
) : ViewModel() {
    private val _state = MutableStateFlow(JourneyState())
    val state: StateFlow<JourneyState> = _state

    val actions = JourneySelectionActions(
        onSetTime = ::setTime,
        onSetTimeType = ::setTimeType,
        onSetDestination = ::setDestination,
        onSetStartLocation = ::setStartLocation,
        onGetJourneyOptions = ::getJourneyOptions,
        onSetJourneyOptions = ::setJourneyOptions
    )

    fun setTimeType(type: TimeType) {
        _state.update { currentState ->
            currentState.copy(filter = currentState.filter.copy(timeType = type))
        }
    }

    fun setTime(time: String) {
        // Attempt to convert time to ISO format string
        var formattedTime = time
        var timeString: String
        try {
            timeString = convertIsoToHhmm(time)
        } catch (e: Exception) {
            try {
                formattedTime = convertHourToInstantIso(time)
                timeString = time
            } catch (e: Exception) {
                Log.e("JourneyViewModel", "Failed to parse time string: ${e.message}")
                return
            }
        }

        Log.d("Journey timeString", timeString)
        Log.d("Journey formattedTime", formattedTime)
        _state.update { currentState ->
            currentState.copy(
                selectedTimeString = timeString,
                filter = currentState.filter.copy(time = formattedTime)
            )
        }
    }

    fun setStartLocation(selectedLocation: SelectedLocation) {
        _state.update { it.copy(startLocation = selectedLocation) }
    }

    fun setDestination(selectedLocation: SelectedLocation) {
        _state.update { it.copy(destination = selectedLocation) }
    }

    fun setJourneyOptions(options: List<JourneyDetails>?) {
        _state.update { it.copy(journeyOptions = options) }
    }

    fun confirmLocationSelection(
        selectedLocation: SelectedLocation, locationType: LocationType, clearSearch: () -> Unit
    ) {
        when (locationType) {
            LocationType.START -> {
                setStartLocation(selectedLocation)
                clearSearch()
                navController.navigate(Screen.JourneySelection.route)
            }

            LocationType.END -> {
                setDestination(selectedLocation)
                clearSearch()
                navController.navigate(Screen.StartLocationSelection.route)
            }
        }
    }

    fun getJourneyOptions() {
        setJourneyOptions(emptyList())
        val start: LatLng? = state.value.startLocation?.latLng
        val destination: LatLng? = state.value.destination?.latLng
        if (start != null && destination != null) {

            viewModelScope.launch {
                val filter = state.value.filter
                val routes =
                    routesManager.getTransitRoutes(
                        start,
                        destination,
                        filter.timeType,
                        filter.resolveTime()
                    )

                val journeyOptions = routes.mapNotNull { route ->
                    val steps = route.legs.firstOrNull()?.steps ?: emptyList()

                    fun getVehicle(step: Step?): Vehicle? {
                        return step?.transitDetails?.transitLine?.vehicle
                    }

                    // firstNodeType: find first TRANSIT step and decide STOP vs STATION
                    val firstTransitStep = steps.firstOrNull { it.travelMode == "TRANSIT" }
                    val firstNodeType = getVehicle(firstTransitStep)?.type?.let { vehicleType ->
                        when (vehicleType.uppercase()) {
                            "HEAVY_RAIL", "RAIL", "SUBWAY", "COMMUTER_RAIL" -> TravelPoint.STATION
                            else -> TravelPoint.STOP
                        }
                    } ?: TravelPoint.STOP

                    // routeSegments: iterate through steps to build string segments
                    var firstWalkOver = false
                    var firstWalkDuration = 0
                    val segments = mutableListOf<String>()
                    steps.forEach { step ->
                        when (step.travelMode) {
                            "WALK" -> {
                                if (segments.lastOrNull() != "Walk") segments.add("Walk")
                                // firstWalkDuration: first WALK step's duration
                                if (!firstWalkOver) firstWalkDuration += step.staticDuration.toSecondsInt()
                            }

                            "TRANSIT" -> {
                                firstWalkOver = true
                                val departureStop =
                                    step.transitDetails?.stopDetails?.departureStop?.name
                                val arrivalStop =
                                    step.transitDetails?.stopDetails?.arrivalStop?.name
                                if (arrivalStop == null || departureStop == null) return@mapNotNull null
                                segments.add(departureStop)
                                segments.add(arrivalStop)
                            }

                            else -> {
                                firstWalkOver = true
                                // Fallback for flexibility - just map generic name
                                segments.add(step.travelMode.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(getDefault())
                                    else it.toString()
                                })
                            }
                        }
                    }
                    firstWalkDuration /= 60

                    // nextDeparture: time until next departure
                    val nextDeparture = Duration.between(
                        Instant.now(),
                        Instant.parse(firstTransitStep?.transitDetails?.stopDetails?.departureTime)
                    )
                    Log.d("Journey Instant.now", Instant.now().toString())
                    Log.d("Journey Instant.parse", Instant.parse(firstTransitStep?.transitDetails?.stopDetails?.departureTime).toString())

                    // arrivalTime: current time + route.duration.value
                    val arrivalTime = calculateArrivalTime(steps)

                    // Filter routes - nextDeparture must exceed walk duration, it must not be
                    // negative, and arrivalTime should not be more than 5 hours from the selected time
                    val arrivalTooFarInFuture =
                        (arrivalTime?.minus(Duration.ofHours(5)) ?: Instant.MAX) > Instant.parse(
                            _state.value.filter.resolveTime()
                        )
                    val departureTooSoonToWalk =
                        nextDeparture.toMinutes() < ceil(0.75 * firstWalkDuration)
                    Log.d("Journey nextDeparture.isNegative", nextDeparture.isNegative.toString())
                    Log.d("Journey departureTooSoonToWalk", departureTooSoonToWalk.toString())
                    Log.d("Journey arrivalTooFarInFuture", arrivalTooFarInFuture.toString())
                    if (nextDeparture.isNegative || departureTooSoonToWalk || arrivalTooFarInFuture) return@mapNotNull null

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
                        firstWalkMinutes = firstWalkDuration,
                        firstNodeType = firstNodeType,
                        routeSegments = segments,
                        nextDeparture = nextDeparture,
                        arrivalTime = arrivalTime,
                        fareTotal = fare
                    )
                }

                if (journeyOptions.isEmpty()) {
                    setJourneyOptions(null)
                } else {
                    setJourneyOptions(journeyOptions)
                }
            }
        }
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

@Stable
data class JourneyState(
    val startLocation: SelectedLocation? = null,
    val destination: SelectedLocation? = null,
    val journeyOptions: List<JourneyDetails>? = emptyList(),
    val selectedTimeString: String = "now",
    val filter: JourneyFilter = JourneyFilter()
)

data class JourneyDetails(
    val firstWalkMinutes: Int?,
    val firstNodeType: TravelPoint?,
    val routeSegments: List<String>?,
    val nextDeparture: Duration?,
    val arrivalTime: Instant?,
    val fareTotal: Float = 0f,
)

data class JourneySelectionActions(
    val onSetTimeType: (TimeType) -> Unit,
    val onSetTime: (String) -> Unit,
    val onSetStartLocation: (SelectedLocation) -> Unit,
    val onSetDestination: (SelectedLocation) -> Unit,
    val onGetJourneyOptions: () -> Unit,
    val onSetJourneyOptions: (List<JourneyDetails>) -> Unit
)

data class JourneyFilter(
    val timeType: TimeType = TimeType.Departure,
    val time: String? = null
) {
    fun resolveTime(): String {
        return if (this.time != null) time else getNearestHalfHour()
    }
}

/**
 * A string list of time slots from "04:30" to "23:00"
 */
val TimeSlots = (9..46).map { interval ->
    val hour = interval / 2
    val minutes = if (interval % 2 == 1) 30 else 0
    LocalTime.of(hour, minutes).format(DateTimeFormatter.ofPattern("HH:mm"))
}

enum class TravelPoint(val label: String) {
    STOP("stop"), STATION("station")
}

enum class TimeType(val label: String) {
    Departure("departure"), Arrival("arrival")
}

enum class LocationType {
    START, END
}