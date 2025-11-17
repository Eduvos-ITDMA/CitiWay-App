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
import com.example.citiway.data.domain.MetrorailService
import com.example.citiway.data.domain.MycitiBusService
import com.example.citiway.data.local.entities.JourneyStep
import com.example.citiway.data.remote.Route
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.data.remote.Step
import com.example.citiway.data.remote.Vehicle
import com.example.citiway.data.repository.CitiWayRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale.getDefault
import java.util.UUID
import kotlin.math.ceil
import com.example.citiway.data.local.entities.Journey as JourneyEntity

class JourneyViewModel(
    private val navController: NavController,
    private val routesManager: RoutesManager = App.appModule.routesManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _state = MutableStateFlow(JourneyState())
    val state: StateFlow<JourneyState> = _state

    // Using Repo to access Db
    private val repository: CitiWayRepository = App.appModule.repository

    var recalculateRoutes = false
    var progressCountdownSeconds = 1L

    // Timer that emits every second, used to update relative times in the UI
    private val ticker = flow {
        while (true) {
            emit(Unit)
            delay(1000L)
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
                        val stops = currentState.journey?.stops?.map { stop ->
                            // Only update times for departures/arrivals that have not passed yet
                            if (stop.nextEventInMin == 0) {
                                stop
                            } else {
                                val nextEventIn =
                                    stop.nextEventIn?.minusSeconds(progressCountdownSeconds)
                                val nextEventInMin = nextEventIn?.toMinutes()?.toInt() ?: 0
                                stop.copy(
                                    nextEventIn = nextEventIn,
                                    nextEventInMin = nextEventInMin,
                                    reached = nextEventInMin <= 0
                                )
                            }
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
        onSetJourney = ::setJourney,
    )

    /**
     * Loads a saved journey from the database for viewing in the summary screen
     */
    fun loadJourneyForSummary(tripId: Int) {
        viewModelScope.launch(dispatcher) {
            try {
                // Fetch the trip first to get basic info
                val trip = repository.getTripById(tripId)
                if (trip == null) {
                    Log.e("JourneyViewModel", "❌ Trip not found for ID: $tripId")
                    return@launch
                }

                // Fetching the journey details
                val journey = repository.getJourneyByTripId(tripId)
                if (journey == null) {
                    Log.e("JourneyViewModel", "❌ Journey not found for trip ID: $tripId")
                    return@launch
                }

                val steps = repository.getStepsForJourney(journey.journey_id)

                // Getting the ACTUAL start and end addresses from journey steps (unlike showing the PitStops on Home)
                val firstStep = steps.minByOrNull { it.step_order }
                val lastStep = steps.maxByOrNull { it.step_order }

                /*
                * Extract the actual start and end addresses from journey steps.
                * - firstStep (step_order = 0): Contains the original walking start address
                * - lastStep (step_order = max): Contains the final walking destination address
                * Falls back to trip table if steps are unavailable (e.g., journey started directly at a station).
                */
                val startAddress = firstStep?.stop_name ?: trip.start_stop ?: "Unknown"
                val endAddress = lastStep?.stop_name ?: trip.end_stop ?: "Unknown"

                // Reconstructing the Journey object from database data
                val reconstructedJourney = reconstructJourneyFromSteps(journey, steps, trip)

                // Reconstruct start and end locations from trip data
                // Use dummy LatLng since we don't need it for summary display in PlacesManager they are non-nullable fields
                val startLocation = SelectedLocation(
                    latLng = LatLng(0.0, 0.0),  // Dummy coordinates - not needed for summary
                    placeId = "",                                   // Empty placeId - not needed for summary
                    primaryText = startAddress                      // From JourneyStep
                )

                val destination = SelectedLocation(
                    latLng = LatLng(0.0, 0.0),  // Dummy coordinates - not needed for summary
                    placeId = "",                                   // Empty placeId - not needed for summary
                    primaryText = endAddress
                )

                // Update state with the loaded journey
                _state.update {
                    it.copy(
                        journey = reconstructedJourney,
                        startLocation = startLocation,
                        destination = destination
                    )
                }

                Log.d("JourneyViewModel", "✅ Journey loaded for trip ID: $tripId")
                Log.d("JourneyViewModel", "📍 Start: $startAddress | End: $endAddress")

            } catch (e: Exception) {
                Log.e("JourneyViewModel", "❌ Failed to load journey: ${e.message}", e)
            }
        }
    }

    /**
     * Reconstructs the Journey object from database steps
     */
    private fun reconstructJourneyFromSteps(
        journey: JourneyEntity?,  // Using the the alias
        steps: List<JourneyStep>?,
        trip: com.example.citiway.data.local.entities.Trip?
    ): Journey? {
        if (journey == null || steps == null) return null

        val stops = steps
            .filter { it.step_type == "STOP" && it.stop_type in listOf("DEPARTURE", "ARRIVAL") }
            .map { step ->
                Stop(
                    name = step.stop_name ?: "",
                    stopType = when(step.stop_type) {
                        "DEPARTURE" -> StopType.DEPARTURE
                        "ARRIVAL" -> StopType.ARRIVAL
                        else -> StopType.DEPARTURE
                    },
                    latLng = null,  // Not needed for summary display
                    nextEventIn = null,  // Not needed for past journeys
                    nextEventInMin = null,
                    routeName = step.route_name,
                    travelMode = step.travel_mode,
                    reached = true,  // All stops reached for completed journey
                    isTransfer = step.is_transfer
                )
            }

        val instructions = steps
            .filter { it.step_type == "INSTRUCTION" }
            .map { step ->
                Instruction(
                    text = step.instruction_text ?: "",
                    durationMinutes = step.duration_minutes ?: 0,
                    travelMode = step.travel_mode ?: "WALK"
                )
            }

        return Journey(
            stops = stops,
            instructions = instructions,
            startTime = Instant.parse(journey.start_time),
            arrivalTime = journey.arrival_time?.let { Instant.parse(it) },
            distanceMeters = journey.distance_meters,
            fareTotal = trip?.total_fare ?: 0.0,  // Get fare from Trip table
            totalStopsCount = journey.total_stops_count
        )
    }

    fun toggleProgressSpeedUp() {
        // Reduce minutes to 2
        _state.update { currentState ->
            val firstDepartureIn = currentState.journey?.stops?.first()?.nextEventInMin ?: 0

            if (firstDepartureIn > 2) {
                val stops = currentState.journey?.stops?.map { stop ->

                    val nextEventIn =
                        stop.nextEventIn?.minusMinutes((firstDepartureIn - 2).toLong())
                    val nextEventInMin = nextEventIn?.toMinutes()?.toInt() ?: 0
                    stop.copy(
                        nextEventIn = nextEventIn,
                        nextEventInMin = nextEventInMin,
                        reached = nextEventInMin == 0
                    )
                }
                val newState = currentState.journey?.copy(stops = stops ?: emptyList())
                currentState.copy(journey = newState)
            } else {
                currentState
            }
        }

        // Increase countdown rate
        progressCountdownSeconds = when (progressCountdownSeconds) {
            1L -> 20L
            20L -> 200L
            200L -> 1L
            else -> 1L
        }
    }

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
        val fareTotal = _state.value.journeyOptions?.find { it.uuid == id }?.fareTotal ?: 0.0

        if (route != null) {
            val journey = routeToJourney(route, fareTotal)
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

            val metrorailService = MetrorailService()
            val mycitiBusService = MycitiBusService()

            viewModelScope.launch(dispatcher) {
                val filter = state.value.filter
                val selectedTime = filter.resolveTime()

                val routes = routesManager.getTransitRoutes(
                    start, destination, filter.timeType, selectedTime
                )
                val routesResponseDataMap = mutableMapOf<String, Route>()

                // Loop through each route in response to parse and filter into a the necessary
                // journeyOptions state field for the UI
                var journeyOptions: List<JourneyDetails>
                try {
                    journeyOptions = routes.mapNotNull { route ->
                        val steps = route.legs.firstOrNull()?.steps ?: emptyList()
                        Log.d("JourneyViewModel steps", steps.toString())

                        // firstNodeType: find first TRANSIT step and decide STOP vs STATION
                        val firstTransitStep = steps.firstOrNull { it.travelMode == "TRANSIT" }
                        val firstNodeType = getVehicle(firstTransitStep)?.type?.let { vehicleType ->
                            when (vehicleType.uppercase()) {
                                "HEAVY_RAIL", "RAIL", "SUBWAY", "COMMUTER_RAIL" -> TravelPoint.STATION
                                else -> TravelPoint.STOP
                            }
                        } ?: TravelPoint.STOP
                        Log.d("JourneyViewModel firstTransitStep", firstTransitStep.toString())

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

                        Log.d(
                            "JourneyViewModel departureTime",
                            firstTransitStep?.transitDetails?.stopDetails?.departureTime ?: "null"
                        )
                        val departureTime =
                            Instant.parse(firstTransitStep?.transitDetails?.stopDetails?.departureTime)
                        val nextDeparture = Duration.between(Instant.now(), departureTime)

                        // arrivalTime: current time + route.duration.value
                        val arrivalTime = calculateArrivalTime(steps)
                        val totalDurationMinutes =
                            steps.sumOf { it.staticDuration.toSecondsInt() } / 60

                        // Filter routes - nextDeparture must exceed walk duration, it must not be
                        // negative, and arrivalTime should not be more than 5 hours from the selected time

                        /* IMPORTANT: removed departureTooSoonToWalk filter for demonstration - Google Maps does
                        is unreliable and often returns routes for the next day even when its only early evening */
                        val arrivalTooFarInFuture = false /*(arrivalTime?.minus(Duration.ofHours(5))
                            ?: Instant.MAX) > Instant.parse(selectedTime)*/
                        val departureTooSoonToWalk =
                            nextDeparture.toMinutes() < ceil(0.75 * firstWalkDuration)

                        if (nextDeparture.isNegative || departureTooSoonToWalk || arrivalTooFarInFuture) {
                            return@mapNotNull null
                        }

                        // Calculate fares
                        steps.forEach { step ->
                            if (step.travelMode == "TRANSIT") {
                                when (getVehicle(step)?.type?.uppercase()) {
                                    "BUS" -> mycitiBusService.adjustFare(step)
                                    "HEAVY_RAIL", "RAIL" -> metrorailService.adjustFare(step)
                                }
                            }
                        }
                        val fareTotal = mycitiBusService.getFare() + metrorailService.getFare()

                        val details = JourneyDetails(
                            firstWalkMinutes = firstWalkDuration,
                            firstNodeType = firstNodeType,
                            routeSegments = segments,
                            nextDeparture = nextDeparture,
                            departureTimeInstant = departureTime,
                            arrivalTime = arrivalTime,
                            fareTotal = fareTotal,
                            totalDurationMinutes = totalDurationMinutes
                        )

                        routesResponseDataMap[details.uuid] = route

                        details
                    }

                    setJourneyOptions(journeyOptions, routesResponseDataMap)
                } catch (e: Exception) {
                    Log.e("JourneyViewModel", "Failed to retrieve and parse Routes: ${e.message}")
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

    fun routeToJourney(route: Route, fareTotal: Double = 0.0): Journey {
        val instructions: MutableList<Instruction> = mutableListOf()
        val stops: MutableList<Stop> = mutableListOf()
        var distance = 0
        var duration = 0
        var fromMode = ""
        val steps = route.legs.firstOrNull()?.steps ?: emptyList()
        val distanceMeters = route.distanceMeters
        val startTime = Instant.now()
        val arrivalTime = calculateArrivalTime(steps)
        var stopsCount = 0

        steps.forEachIndexed { index, step ->
            when (step.travelMode) {
                "WALK" -> {
                    distance += step.distanceMeters
                    duration += step.staticDuration.toSecondsInt()
                    fromMode = "WALK"
                }

                "TRANSIT" -> {
                    stopsCount += step.transitDetails?.stopCount ?: 0
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
                            nextEventInMin,
                            null, // routeName not needed for arrival
                            vehicleType,
                        )
                    )

                    distance = 0
                    duration = 0
                }

                else -> throw Exception("Unexpected travel mode '${step.travelMode}'")
            }
        }

        // ======== Detecting transfers at same location ========
        // Marked ARRIVAL stops that are followed by DEPARTURE at the same location as transfers
        for (i in 0 until stops.size - 1) {
            val currentStop = stops[i]
            val nextStop = stops[i + 1]

            if (currentStop.stopType == StopType.ARRIVAL &&
                nextStop.stopType == StopType.DEPARTURE &&
                currentStop.name == nextStop.name
            ) {
                // This is a transfer - update the stop
                stops[i] = currentStop.copy(isTransfer = true)
            }
        }

        // Add final walk instruction, if applicable
        if (fromMode == "WALK") {
            instructions.add(Instruction("Walk ${distance}m", duration / 60, "WALK"))
        }

        return Journey(stops, instructions, startTime, arrivalTime, distanceMeters, fareTotal, stopsCount)
    }

    /**
     * Saves the completed journey to the database.
     * Called when the user reaches their final destination.
     */
    suspend fun saveCompletedJourney(userId: Int) {
        val currentJourney = _state.value.journey ?: run {
            Log.e("JourneyViewModel", "Cannot save: No journey data")
            return
        }

        val startLocation = _state.value.startLocation ?: run {
            Log.e("JourneyViewModel", "Cannot save: No start location")
            return
        }

        val destination = _state.value.destination ?: run {
            Log.e("JourneyViewModel", "Cannot save: No destination")
            return
        }

        viewModelScope.launch(dispatcher) {
            try {
                // 1. Build and save Trip
                val trip = buildTripFromJourney(userId, currentJourney, startLocation, destination)
                val routes = buildRoutesFromJourney(currentJourney)
                val tripId = repository.saveCompletedJourney(trip, routes).toInt()

                // 2. Build and save Journey
                val journey = JourneyEntity(  // Use the alias
                    trip_id = tripId,
                    start_time = currentJourney.startTime.toString(),
                    arrival_time = currentJourney.arrivalTime?.toString(),
                    distance_meters = currentJourney.distanceMeters,
                    total_stops_count = currentJourney.totalStopsCount
                )
                val journeyId = repository.insertJourney(journey).toInt()

                // 3. Build and save JourneySteps (alternating stops and instructions)
                val steps = buildJourneySteps(journeyId, currentJourney, startLocation, destination)
                repository.insertJourneySteps(steps)

                Log.d("JourneyViewModel", "✅ Full journey saved with ${steps.size} steps")

            } catch (e: Exception) {
                Log.e("JourneyViewModel", "❌ Failed to save journey", e)
            }
        }
    }

    private fun buildJourneySteps(
        journeyId: Int,
        journey: Journey,
        startLocation: SelectedLocation,
        destination: SelectedLocation
    ): List<JourneyStep> {
        val steps = mutableListOf<JourneyStep>()
        var order = 0

        // Start location
        steps.add(
            JourneyStep(
                journey_id = journeyId,
                step_order = order++,
                step_type = "STOP",
                stop_name = startLocation.primaryText,
                stop_type = "START",
                route_name = null,
                travel_mode = null,
                is_transfer = false,
                instruction_text = null,
                duration_minutes = null
            )
        )

        // Add first instruction
        journey.instructions.firstOrNull()?.let { instruction ->
            steps.add(
                JourneyStep(
                    journey_id = journeyId,
                    step_order = order++,
                    step_type = "INSTRUCTION",
                    stop_name = null,
                    stop_type = null,
                    route_name = null,
                    travel_mode = instruction.travelMode,
                    is_transfer = false,
                    instruction_text = instruction.text,
                    duration_minutes = instruction.durationMinutes
                )
            )
        }

        // Add alternating stops and instructions
        journey.stops.forEachIndexed { index, stop ->
            // Add stop
            steps.add(
                JourneyStep(
                    journey_id = journeyId,
                    step_order = order++,
                    step_type = "STOP",
                    stop_name = stop.name,
                    stop_type = stop.stopType.name,
                    route_name = stop.routeName,
                    travel_mode = stop.travelMode,
                    is_transfer = stop.isTransfer,
                    instruction_text = null,
                    duration_minutes = null
                )
            )

            // Add corresponding instruction (skip last one)
            journey.instructions.getOrNull(index + 1)?.let { instruction ->
                steps.add(
                    JourneyStep(
                        journey_id = journeyId,
                        step_order = order++,
                        step_type = "INSTRUCTION",
                        stop_name = null,
                        stop_type = null,
                        route_name = null,
                        travel_mode = instruction.travelMode,
                        is_transfer = false,
                        instruction_text = instruction.text,
                        duration_minutes = instruction.durationMinutes
                    )
                )
            }
        }

        // End location
        steps.add(
            JourneyStep(
                journey_id = journeyId,
                step_order = order++,
                step_type = "STOP",
                stop_name = destination.primaryText,
                stop_type = "END",
                route_name = null,
                travel_mode = null,
                is_transfer = false,
                instruction_text = null,
                duration_minutes = null
            )
        )

        return steps
    }

    /**
     * Called when the journey is complete. Saves to database and navigates to summary.
     */
    fun onJourneyComplete() {
        viewModelScope.launch(dispatcher) {
            try {
                // TODO: Get actual user ID from system, but its always 1
                val userId = 1  // Hardcoded for now

                saveCompletedJourney(userId)

                Log.d("JourneyViewModel", "✅ Journey completed and saved!")

                // Navigate to summary screen
                navController.navigate(Screen.JourneySummary.route)

            } catch (e: Exception) {
                Log.e("JourneyViewModel", "❌ Failed to complete journey: ${e.message}", e)
                // Still navigate even if save fails (user experience)
                navController.navigate(Screen.JourneySummary.route)
            }
        }
    }

    fun clearJourneyState() {
        _state.update {
            it.copy(
                journey = null,
                startLocation = null,
                destination = null,
                journeyOptions = null,
                routesResponse = null
            )
        }
    }

    /**
     * Builds a Trip entity from journey data
     */
    private fun buildTripFromJourney(
        userId: Int,
        journey: Journey,
        startLocation: SelectedLocation,
        destination: SelectedLocation
    ): com.example.citiway.data.local.entities.Trip {
        // Determine overall trip mode
        val modes = journey.instructions
            .map { it.travelMode }
            .filter { it != "WALK" }
            .distinct()

        val tripMode = when {
            modes.isEmpty() -> "Walk"
            modes.size == 1 -> when(modes[0]) {
                "HEAVY_RAIL" -> "Train"
                "BUS" -> "Bus"
                else -> "Other"
            }
            else -> "Multi"
        }

        val totalDurationMinutes = journey.instructions.sumOf { it.durationMinutes }
        val dateString = java.time.LocalDate.now().toString()

        // Get neighborhood from first/last stop names instead
        val startNeighborhood = journey.stops.firstOrNull()?.name ?: startLocation.primaryText ?: "Unknown"
        val endNeighborhood = journey.stops.lastOrNull()?.name ?: destination.primaryText ?: "Unknown"

        // Round distance to 2 decimal places
        val distanceKm = kotlin.math.round(journey.distanceMeters / 1000.0 * 100) / 100

        return com.example.citiway.data.local.entities.Trip(
            user_id = userId,
            start_stop = startNeighborhood,
            end_stop = endNeighborhood,
            date = dateString,
            trip_time = "${totalDurationMinutes} min",
            mode = tripMode,
            total_distance_km = distanceKm,  // Maybe we use .5 rounds
            total_fare = journey.fareTotal,
            is_favourite = false,
            created_at = System.currentTimeMillis()
        )
    }

    /**
     * Extracts route entities from journey data
     */
    private fun buildRoutesFromJourney(journey: Journey): List<com.example.citiway.data.local.entities.Route> {
        val routes = mutableListOf<com.example.citiway.data.local.entities.Route>()

        val originalRoute = _state.value.routesResponse?.values?.firstOrNull()
        val steps = originalRoute?.legs?.firstOrNull()?.steps ?: emptyList()

        var currentDepartureStop: Stop? = null
        var stepIndex = 0

        journey.stops.forEach { stop ->
            when (stop.stopType) {
                StopType.DEPARTURE -> {
                    currentDepartureStop = stop
                }

                StopType.ARRIVAL -> {
                    if (currentDepartureStop != null && !stop.isTransfer) {
                        val transitStep = steps.getOrNull(stepIndex)
                        val distanceMeters = transitStep?.distanceMeters ?: 0

                        // Rounding distance to 2 decimal places
                        val distanceKm = kotlin.math.round(journey.distanceMeters / 1000.0 * 100) / 100

                        val providerId = when (stop.travelMode?.uppercase()) {
                            "BUS" -> 1
                            "HEAVY_RAIL", "RAIL" -> 2
                            else -> null
                        }

                        val modeString = when (stop.travelMode?.uppercase()) {
                            "BUS" -> "bus"
                            "HEAVY_RAIL", "RAIL" -> "train"
                            else -> "other"
                        }

                        val myCitiFareId: Int?
                        val metrorailFareId: Int?

                        when (stop.travelMode?.uppercase()) {
                            "BUS" -> {
                                val fareData = runBlocking { repository.getMyCitiFare(distanceMeters) }
                                myCitiFareId = fareData?.myciti_fare_id
                                metrorailFareId = null
                            }
                            "HEAVY_RAIL", "RAIL" -> {
                                myCitiFareId = null
                                metrorailFareId = 1  // Default to Zone 1 for now
                            }
                            else -> {
                                myCitiFareId = null
                                metrorailFareId = null
                            }
                        }

                        routes.add(
                            com.example.citiway.data.local.entities.Route(
                                trip_id = null,  // Will be set by repository transaction
                                provider_id = providerId,
                                start_location = currentDepartureStop.name,
                                destination = stop.name,
                                mode = modeString,
                                distance_km = distanceKm,
                                fare_contribution = null,
                                schedule = null,
                                myciti_fare_id = myCitiFareId,
                                metrorail_fare_id = metrorailFareId
                            )
                        )

                        currentDepartureStop = null
                        stepIndex++
                    }
                }
            }
        }

        return routes
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
    val fareTotal: Double = 0.0,
    val totalDurationMinutes: Int? = null
)

data class JourneySelectionActions(
    val onSetTimeType: (TimeType) -> Unit,
    val onSetTime: (String) -> Unit,
    val onSetStartLocation: (SelectedLocation) -> Unit,
    val onSetDestination: (SelectedLocation) -> Unit,
    val onGetJourneyOptions: () -> Unit,
    val onSetJourneyOptions: (List<JourneyDetails>, Map<String, Route>) -> Unit,
    val onSetJourney: (id: String) -> Unit,
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
    val startTime: Instant,
    val arrivalTime: Instant?,
    val distanceMeters: Int,
    val fareTotal: Double,
    val totalStopsCount: Int = 0
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
    val isTransfer: Boolean = false, // Indicates transfer at same location (bus or train station)
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