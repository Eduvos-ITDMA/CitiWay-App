package com.example.citiway.features.shared

import android.util.Log
import androidx.navigation.NavController
import com.example.citiway.data.remote.Line
import com.example.citiway.data.remote.LocalizedText
import com.example.citiway.data.remote.LocalizedTime
import com.example.citiway.data.remote.Location
import com.example.citiway.data.remote.Polyline
import com.example.citiway.data.remote.Route
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.RoutesResponse
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.data.remote.Step
import com.example.citiway.data.remote.TransitDetails
import com.example.citiway.data.remote.TransitDetailsLocalizedValues
import com.example.citiway.data.remote.TransitStop
import com.example.citiway.data.remote.TransitStopDetails
import com.example.citiway.data.remote.Vehicle
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.io.InputStreamReader
import java.time.Duration
import java.time.Instant

class JourneyViewModelTest {
    private lateinit var mockNavController: NavController
    private lateinit var mockRoutesManager: RoutesManager
    private lateinit var testDispatcher: TestDispatcher

    // The class we are testing
    private lateinit var viewModel: JourneyViewModel

    // This function runs before each test
    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        // Create mock (fake) versions of the dependencies
        mockNavController = mockk(relaxed = true)
        mockRoutesManager = mockk(relaxed = true)

        testDispatcher = StandardTestDispatcher()
        // Initialize the ViewModel with the mock dependencies
        viewModel = JourneyViewModel(mockNavController, mockRoutesManager, testDispatcher)
    }

    @Test
    fun `routeToJourney should correctly convert a Route object to a Journey object`() {
        // ARRANGE
        val testRoute = loadFirstRouteFromJson("sample_routes_response.json")

        // ACT
        val journey = viewModel.routeToJourney(testRoute)

        // ASSERT
        assertEquals("Walk 374m", journey.instructions[0].text)
        assertEquals(4, journey.instructions[0].durationMinutes)
        assertEquals("WALK", journey.instructions[0].travelMode)

        // Second instruction: The first TRANSIT step
        assertEquals("Take train for 5 stations", journey.instructions[1].text)
        assertEquals(9, journey.instructions[1].durationMinutes) // 540s / 60
        assertEquals("HEAVY_RAIL", journey.instructions[1].travelMode)

        // Third instruction: A combination of the next four WALK steps
        assertEquals("Walk 341m", journey.instructions[2].text)
        assertEquals(4, journey.instructions[2].durationMinutes)
        assertEquals("WALK", journey.instructions[2].travelMode)

        // Fourth instruction: The final TRANSIT step
        assertEquals("Take MyCiTi bus for 15 stops", journey.instructions[3].text)
        assertEquals(24, journey.instructions[3].durationMinutes)
        assertEquals("BUS", journey.instructions[3].travelMode)

        assertEquals("Walk 569m", journey.instructions[6].text)
        assertEquals(7, journey.instructions[6].durationMinutes)
        assertEquals("WALK", journey.instructions[6].travelMode)

        // Assertions for the stops
        assertEquals(6, journey.stops.size)
        assertEquals("Rondebosch Station", journey.stops[0].name)
        assertEquals(StopType.DEPARTURE, journey.stops[0].stopType)
        assertEquals("Southern Line", journey.stops[0].routeName)
        assertEquals("HEAVY_RAIL", journey.stops[0].travelMode)

        assertEquals("Salt River Station", journey.stops[1].name)
        assertEquals(StopType.ARRIVAL, journey.stops[1].stopType)
        assertEquals(null, journey.stops[1].routeName)
        assertEquals(null, journey.stops[1].travelMode)

        assertEquals("Zastron", journey.stops[4].name)
        assertEquals(StopType.DEPARTURE, journey.stops[4].stopType)
        assertEquals("T04", journey.stops[4].routeName)
        assertEquals("BUS", journey.stops[4].travelMode)

        assertEquals("Sanddrift", journey.stops[5].name)
        assertEquals(StopType.ARRIVAL, journey.stops[5].stopType)
        assertEquals(null, journey.stops[5].routeName)
        assertEquals(null, journey.stops[5].travelMode)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getJourneyOptions should update state with journey options when called`() = runTest(testDispatcher) {
        // ARRANGE
        val mockLocation = SelectedLocation(
            placeId = "mock id",
            primaryText = "Mock Location",
            latLng = LatLng(0.0, 0.0)
        )
        viewModel.setStartLocation(mockLocation)
        viewModel.setDestination(mockLocation)

        val mockRoutes = loadRoutesFromJson("sample_routes_response.json")
        coEvery {
            mockRoutesManager.getTransitRoutes(
                any(),
                any(),
                any(),
                any()
            )
        } returns mockRoutes

        // ACT
        viewModel.getJourneyOptions()
        advanceUntilIdle()

        // ASSERT
        val journeyOptions = viewModel.state.value.journeyOptions
        assertNotNull(journeyOptions) // Ensure journey options are not null
        assertEquals(journeyOptions!!.size, 6) // Ensure there are options

        // First route
        assertEquals(4, journeyOptions[0].firstWalkMinutes)
        assertEquals(TravelPoint.STATION, journeyOptions[0].firstNodeType)
        assertEquals(
            listOf(
                "Walk",
                "Rondebosch Station",
                "Salt River Station",
                "Walk",
                "Salt River Rail North",
                "Zastron",
                "Walk",
                "Zastron",
                "Sanddrift",
                "Walk"
            ), journeyOptions[0].routeSegments
        )
        val nextDeparture =
            Duration.between(Instant.now(), Instant.parse("2025-10-16T15:17:30Z"))
        assertEquals(nextDeparture, journeyOptions[0].nextDeparture)
        // Fare calculation is a WIP - mock fare total used for now
        assertEquals(50f, journeyOptions[0].fareTotal)
        assertEquals(68, journeyOptions[0].totalDurationMinutes)

        // Third route
        assertEquals(4, journeyOptions[2].firstWalkMinutes)
        assertEquals(TravelPoint.STATION, journeyOptions[2].firstNodeType)
        assertEquals(
            listOf(
                "Walk",
                "Rondebosch Station",
                "Woodstock Station",
                "Walk",
                "Davison",
                "Mansfield",
                "Walk",
                "Mansfield",
                "Century City",
                "Walk"
            ), journeyOptions[2].routeSegments
        )
        val thirdRouteNextDeparture =
            Duration.between(Instant.now(), Instant.parse("2025-10-16T15:37:30Z"))
        assertEquals(thirdRouteNextDeparture, journeyOptions[2].nextDeparture)
        assertEquals(50f, journeyOptions[2].fareTotal)
        assertEquals(80, journeyOptions[2].totalDurationMinutes)

        // Fifth route
        assertEquals(280, journeyOptions[4].firstWalkMinutes)
        assertEquals(TravelPoint.STATION, journeyOptions[4].firstNodeType)
        assertEquals(
            listOf(
                "Walk",
                "Belmont",
                "Mowbray",
                "Walk",
                "Mowbray Rail Main",
                "Civic Centre",
                "Walk",
                "Civic Centre",
                "Sanddrift",
                "Walk"
            ), journeyOptions[4].routeSegments
        )
        val fifthRouteNextDeparture =
            Duration.between(Instant.now(), Instant.parse("2025-10-16T15:19:00Z"))
        assertEquals(fifthRouteNextDeparture, journeyOptions[4].nextDeparture)
        assertEquals(50f, journeyOptions[4].fareTotal)
        assertEquals(74, journeyOptions[4].totalDurationMinutes)
    }

    private fun loadFirstRouteFromJson(fileName: String): Route {
        val response = loadRoutesFromJson(fileName)
        return response.first()
    }

    @Test
    fun `calculateArrivalTime should return correct arrival time for transit and walk steps`() {
        // ARRANGE
        val arrivalInstant = Instant.parse("2025-10-16T15:00:00Z")

        val transitStop = TransitStop(name = "Station A", location = Location(LatLng(0.0, 0.0)))
        val departureStop = TransitStop(name = "Station B", location = Location(LatLng(0.0, 0.0)))

        // Create the transit step with arrival time
        val transitStep = Step(
            travelMode = "TRANSIT",
            distanceMeters = 0,
            staticDuration = "600s",
            polyline = Polyline(""),
            transitDetails = TransitDetails(
                stopDetails = TransitStopDetails(
                    arrivalStop = transitStop,
                    arrivalTime = arrivalInstant.toString(),
                    departureStop = departureStop,
                    departureTime = "2025-10-16T14:50:00Z"
                ),
                localizedValues = TransitDetailsLocalizedValues(
                    arrivalTime = LocalizedTime(LocalizedText("3:00 PM", "en"), "UTC"),
                    departureTime = LocalizedTime(LocalizedText("2:50 PM", "en"), "UTC")
                ),
                headsign = "Main Line",
                transitLine = Line(
                    name = "Main Line", vehicle = Vehicle(
                        name = LocalizedText(
                            "Bus",
                            "en"
                        ), type = "BUS"
                    )
                ),
                stopCount = 5
            ),
            startLocation = Location(LatLng(0.0, 0.0)),
            endLocation = Location(LatLng(0.0, 0.0))
        )

        // Create the walk step
        val firstWalkStep = Step(
            travelMode = "WALK",
            distanceMeters = 0,
            staticDuration = "300s",
            polyline = Polyline(""),
            transitDetails = null,
            startLocation = Location(LatLng(0.0, 0.0)),
            endLocation = Location(LatLng(0.0, 0.0))
        )

        val secondWalkStep = Step(
            travelMode = "WALK",
            distanceMeters = 0,
            staticDuration = "480s",
            polyline = Polyline(""),
            transitDetails = null,
            startLocation = Location(LatLng(0.0, 0.0)),
            endLocation = Location(LatLng(0.0, 0.0))
        )

        val steps = listOf(transitStep, firstWalkStep, secondWalkStep)

        // ACT
        val arrivalTime = viewModel.calculateArrivalTime(steps)

        // ASSERT
        // Walk should add 5 + 8 minutes to the arrival time
        val expectedArrivalTime = arrivalInstant.plusSeconds(13 * 60) // 13 minutes in seconds
        assertEquals(expectedArrivalTime, arrivalTime)
    }

    private fun loadRoutesFromJson(fileName: String): List<Route> {
        val inputStream = this.javaClass.classLoader!!.getResourceAsStream(fileName)
        val reader = InputStreamReader(inputStream)
        // Use Gson to parse the JSON into our data class
        val response = Gson().fromJson(reader, RoutesResponse::class.java)
        reader.close()

        return response.routes
    }
}