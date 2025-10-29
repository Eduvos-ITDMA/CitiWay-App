package com.example.citiway.features.shared

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.convertHourToInstantIso
import com.example.citiway.core.utils.convertIsoToHhmm
import com.example.citiway.core.utils.getNearestHalfHour
import com.example.citiway.core.utils.toSecondsInt
import com.example.citiway.data.local.entities.MyCitiFare
import com.example.citiway.data.remote.Route
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.data.remote.Step
import com.example.citiway.data.remote.Vehicle
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale.getDefault
import java.util.UUID
import kotlin.math.ceil

class JourneyViewModel(
    private val navController: NavController,
    private val routesManager: RoutesManager = App.appModule.routesManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _state = MutableStateFlow(JourneyState())
    val state: StateFlow<JourneyState> = _state

    private val scope = CoroutineScope(viewModelScope.coroutineContext)
    var recalculateRoutes = false

    // Timer that emits every second, used to update relative times in the UI
    private val ticker = flow {
        while (true) {
            emit(Unit)
            delay(1000)
        }
    }

    init {
        viewModelScope.launch {
            // Recalculate the nextDeparture duration from the original departure time
            // This triggers a recomposition in the UI when the minute ticks down
            ticker.collect {
                // For times on Journey Selection Screen
                if (_state.value.journeyOptions.isNullOrEmpty()) {
                    _state.update { currentState ->
                        val updatedOptions = currentState.journeyOptions?.map { details ->
                            val newNextDeparture = Duration.between(
                                Instant.now(), details.departureTimeInstant
                            )
                            details.copy(nextDeparture = newNextDeparture)
                        }
                        currentState.copy(journeyOptions = updatedOptions)
                    }
                }

                // For times on Progress Tracker Screen
                if (_state.value.journey != null) {
                    _state.update { currentState ->
                        val stops = currentState.journey?.stops?.mapNotNull { stop ->
                            // Only update times for departures/arrivals that have not passed yet
                            if (stop.nextEventInMin == 0) return@mapNotNull null

                            val nextEventIn = stop.nextEventIn?.minusSeconds(1)
                            val nextEventInMin = nextEventIn?.toMinutes()?.toInt() ?: 0
                            stop.copy(
                                nextEventIn = nextEventIn,
                                nextEventInMin = nextEventInMin,
                                reached = nextEventInMin == 0
                            )
                        }
                        val newState = currentState.journey?.copy(stops = stops ?: emptyList())
                        currentState.copy(journey = newState)
                    }
                }

            }
        }
    }

    val actions = JourneySelectionActions(
        onSetTime = ::setTime,
        onSetTimeType = ::setTimeType,
        onSetDestination = ::setDestination,
        onSetStartLocation = ::setStartLocation,
        onGetJourneyOptions = ::getJourneyOptions,
        onSetJourneyOptions = ::setJourneyOptions,
        onSetJourney = ::setJourney
    )

    fun setTimeType(type: TimeType) {
        _state.update { currentState ->
            currentState.copy(filter = currentState.filter.copy(timeType = type))
        }
    }

    fun setTime(time: String) {
        // Attempt to convert time to ISO format string
        var isoTimeString = time
        var timeString: String
        try {
            timeString = convertIsoToHhmm(time)
        } catch (e: Exception) {
            try {
                isoTimeString = convertHourToInstantIso(time)
                timeString = time
            } catch (e: Exception) {
                Log.e("JourneyViewModel", "Failed to parse time string: ${e.message}")
                return
            }
        }

        _state.update { currentState ->
            currentState.copy(
                selectedTimeString = timeString,
                filter = currentState.filter.copy(time = isoTimeString)
            )
        }

        recalculateRoutes = true
    }

    fun setStartLocation(selectedLocation: SelectedLocation) {
        _state.update { it.copy(startLocation = selectedLocation) }
    }

    fun setDestination(selectedLocation: SelectedLocation) {
        _state.update { it.copy(destination = selectedLocation) }
        recalculateRoutes = true
    }

    fun setJourneyOptions(
        options: List<JourneyDetails> = emptyList(), routesResponse: Map<String, Route> = emptyMap()
    ) {
        _state.update {
            it.copy(journeyOptions = options, routesResponse = routesResponse)
        }
        recalculateRoutes = true
    }

    private fun clearJourneyOptions() {
        _state.update {
            it.copy(journeyOptions = null, routesResponse = null)
        }
        recalculateRoutes = true
    }

    fun setJourney(id: String) {
        val route = _state.value.routesResponse?.get(id)

        if (route != null) {
            val journey = routeToJourney(route)
            _state.update { it.copy(journey = journey) }
        }
    }

    fun getVehicle(step: Step?): Vehicle? {
        return step?.transitDetails?.transitLine?.vehicle
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
                if (_state.value.startLocation != null) {
                    navController.navigate(Screen.JourneySelection.route)
                } else {
                    navController.navigate(Screen.StartLocationSelection.route)
                }
            }
        }
    }

    fun getJourneyOptions() {
        // Do not make request unless the time, start location, or destination has changed
        if (!recalculateRoutes) return
        recalculateRoutes = false

        clearJourneyOptions()
        val start: LatLng? = state.value.startLocation?.latLng
        val destination: LatLng? = state.value.destination?.latLng
        if (start != null && destination != null) {

            scope.launch(dispatcher) {
                val filter = state.value.filter

                val routes = routesManager.getTransitRoutes(
                    start, destination, filter.timeType, filter.resolveTime()
                )
                val routesResponseDataMap = mutableMapOf<String, Route>()

                // Loop through each route in response to parse and filter into a the necessary
                // journeyOptions state field for the UI
                var journeyOptions: List<JourneyDetails>
                try {
                    journeyOptions = routes.mapNotNull { route ->
                        val steps = route.legs.firstOrNull()?.steps ?: emptyList()

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

                        val departureTime =
                            firstTransitStep?.transitDetails?.stopDetails?.departureTime
                        val departureInstant = Instant.parse(departureTime)

                        // nextDeparture: time until next departure
                        val nextDeparture =
                            Duration.between(Instant.now(), Instant.parse(departureTime))

                        // arrivalTime: current time + route.duration.value
                        val arrivalTime = calculateArrivalTime(steps)
                        val totalDurationMinutes =
                            steps.sumOf { it.staticDuration.toSecondsInt() } / 60

                        // Filter routes - nextDeparture must exceed walk duration, it must not be
                        // negative, and arrivalTime should not be more than 5 hours from the selected time
                        val arrivalTooFarInFuture = (arrivalTime?.minus(Duration.ofHours(5))
                            ?: Instant.MAX) > Instant.parse(
                            _state.value.filter.resolveTime()
                        )
                        val departureTooSoonToWalk =
                            nextDeparture.toMinutes() < ceil(0.75 * firstWalkDuration)
                        if (nextDeparture.isNegative || departureTooSoonToWalk || arrivalTooFarInFuture) return@mapNotNull null

                        // TODO: fareTotal
                        var fare = 0.0f
                        steps.forEach { step ->
                            if (step.travelMode == "TRANSIT") {
                                var MyCitiFare =
                                when (getVehicle(step)?.type?.uppercase()) {
                                    "BUS" ->
                                    "HEAVY_RAIL", "RAIL" -> fare += 10f
                                    else -> fare = 0f
                                }
                            }
                        }

                        val details = JourneyDetails(
                            firstWalkMinutes = firstWalkDuration,
                            firstNodeType = firstNodeType,
                            routeSegments = segments,
                            nextDeparture = nextDeparture,
                            departureTimeInstant = departureInstant,
                            arrivalTime = arrivalTime,
                            fareTotal = fare,
                            totalDurationMinutes = totalDurationMinutes
                        )

                        routesResponseDataMap[details.uuid] = route

                        details
                    }

                    setJourneyOptions(journeyOptions, routesResponseDataMap)
                } catch (e: Exception) {
                    setJourneyOptions()
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

    fun routeToJourney(route: Route): Journey {
        val instructions: MutableList<Instruction> = mutableListOf()
        val stops: MutableList<Stop> = mutableListOf()
        var distance = 0
        var duration = 0
        var fromMode = ""
        val steps = route.legs.firstOrNull()?.steps ?: emptyList()
        val distanceMeters = route.distanceMeters
        val arrivalTime = calculateArrivalTime(steps)

        steps.forEachIndexed { index, step ->
            when (step.travelMode) {
                "WALK" -> {
                    distance += step.distanceMeters
                    duration += step.staticDuration.toSecondsInt()
                    fromMode = "WALK"
                }

                "TRANSIT" -> {
                    // ======== Add walk instruction ========
                    val minutes = duration / 60
                    if (fromMode == "WALK") {
                        instructions.add(Instruction("Walk ${distance}m", minutes, "WALK"))
                    }

                    val stopDetails = step.transitDetails?.stopDetails
                    val vehicleType = getVehicle(step)?.type ?: ""

                    // ========  Get departure stop data ========
                    var stopName = stopDetails?.departureStop?.name ?: "Transport stop"
                    var latLng = stopDetails?.departureStop?.location?.latLng
                    var nextEventIn = Duration.between(
                        Instant.now(),
                        Instant.parse(stopDetails?.departureTime)
                    )
                    var nextEventInMin = nextEventIn.toMinutes().toInt()
                    val routeName = if (vehicleType == "BUS") {
                        step.transitDetails?.transitLine?.nameShort ?: ""
                    } else {
                        step.transitDetails?.transitLine?.name ?: ""
                    }

                    // Add departure stop
                    stops.add(
                        Stop(
                            stopName,
                            StopType.DEPARTURE,
                            latLng,
                            nextEventIn,
                            nextEventInMin,
                            routeName,
                            vehicleType,
                        )
                    )

                    // ======== Get intermediary instruction data ========
                    val stopCount = step.transitDetails?.stopCount ?: 1
                    var instructionText = when (vehicleType) {
                        "HEAVY_RAIL" -> "Take train for $stopCount stations"
                        "BUS" -> "Take MyCiTi bus for $stopCount stops"
                        else -> "Take transport"
                    }
                    if (stopCount <= 1) instructionText = instructionText.dropLast(1)

                    // Add intermediary instruction
                    instructions.add(
                        Instruction(
                            instructionText,
                            step.staticDuration.toSecondsInt() / 60,
                            vehicleType,
                        )
                    )

                    // ======== Get arrival stop data ========
                    stopName = stopDetails?.arrivalStop?.name ?: "Transport stop"
                    latLng = stopDetails?.arrivalStop?.location?.latLng
                    nextEventIn = Duration.between(
                        Instant.now(),
                        Instant.parse(stopDetails?.arrivalTime)
                    )
                    nextEventInMin = nextEventIn.toMinutes().toInt()

                    // Add arrival stop
                    stops.add(
                        Stop(
                            stopName,
                            StopType.ARRIVAL,
                            latLng,
                            nextEventIn,
                            nextEventInMin
                        )
                    )

                    distance = 0
                    duration = 0
                }

                else -> throw Exception("Unexpected travel mode '${step.travelMode}'")
            }
        }

        // Add final walk instruction, if applicable
        if (fromMode == "WALK") {
            instructions.add(Instruction("Walk ${distance}m", duration / 60, "WALK"))
        }

        return Journey(stops, instructions, arrivalTime, distanceMeters)
    }
}

@Stable
data class JourneyState(
    val startLocation: SelectedLocation? = null,
    val destination: SelectedLocation? = null,
    val journeyOptions: List<JourneyDetails>? = null,
    val journey: Journey? = null,
    val routesResponse: Map<String, Route>? = null,
    val selectedTimeString: String = "now",
    val filter: JourneyFilter = JourneyFilter()
)

data class JourneyDetails(
    val uuid: String = UUID.randomUUID().toString(),
    val firstWalkMinutes: Int?,
    val firstNodeType: TravelPoint?,
    val routeSegments: List<String>?,
    val nextDeparture: Duration?,
    val departureTimeInstant: Instant,
    val arrivalTime: Instant?,
    val fareTotal: Float = 0f,
    val totalDurationMinutes: Int? = null
)

data class JourneySelectionActions(
    val onSetTimeType: (TimeType) -> Unit,
    val onSetTime: (String) -> Unit,
    val onSetStartLocation: (SelectedLocation) -> Unit,
    val onSetDestination: (SelectedLocation) -> Unit,
    val onGetJourneyOptions: () -> Unit,
    val onSetJourneyOptions: (List<JourneyDetails>, Map<String, Route>) -> Unit,
    val onSetJourney: (id: String) -> Unit
)

data class JourneyFilter(
    val timeType: TimeType = TimeType.Departure, val time: String? = null
) {
    fun resolveTime(): String {
        return if (this.time != null) time else getNearestHalfHour()
    }
}

data class Journey(
    val stops: List<Stop>,
    val instructions: List<Instruction>,
    val arrivalTime: Instant?,
    val distanceMeters: Int
)

data class Stop(
    val name: String,
    val stopType: StopType,
    val latLng: LatLng?,
    val nextEventIn: Duration? = null,
    val nextEventInMin: Int? = null,
    val routeName: String? = null,
    val travelMode: String? = null,
    var reached: Boolean = false,
)

data class Instruction(
    var text: String,
    val durationMinutes: Int,
    val travelMode: String = "WALK",
)

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

enum class StopType {
    DEPARTURE, ARRIVAL
}