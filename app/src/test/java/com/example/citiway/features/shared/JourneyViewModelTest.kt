package com.example.citiway.features.shared

import android.util.Log
import androidx.navigation.NavController
import com.example.citiway.data.remote.Route
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.RoutesResponse
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.io.InputStreamReader

class JourneyViewModelTest {
    private lateinit var mockNavController: NavController
    private lateinit var mockRoutesManager: RoutesManager

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

        // Initialize the ViewModel with the mock dependencies
        viewModel = JourneyViewModel(mockNavController, mockRoutesManager)
    }

    @Test
    fun `routeToJourney should correctly convert a Route object to a Journey object`() {
        // ARRANGE
        val testRoute = loadRouteFromJson("sample_routes_response.json")

        // ACT
        val journey = viewModel.routeToJourney(testRoute)

        // ASSERT
        assertEquals("Walk 374m", journey.instructions[0].text)
        assertEquals(
            4, journey.instructions[0].durationMinutes
        ) // (182s + 98s) / 60 = 4.66, rounded up
        assertEquals("WALK", journey.instructions[0].travelMode)

        // Second instruction: The first TRANSIT step
        assertEquals("Take train for 5 stations", journey.instructions[1].text)
        assertEquals(9, journey.instructions[1].durationMinutes) // 540s / 60
        assertEquals("HEAVY_RAIL", journey.instructions[1].travelMode)

        // Third instruction: A combination of the next four WALK steps
        assertEquals("Walk 341m", journey.instructions[2].text)
        assertEquals(
            4, journey.instructions[2].durationMinutes
        ) // (15s + 248s + 20s + 7s) / 60 = 4.83, rounded up
        assertEquals("WALK", journey.instructions[2].travelMode)

        // Fourth instruction: The final TRANSIT step
        assertEquals("Take MyCiTi bus for 15 stops", journey.instructions[3].text)
        assertEquals(24, journey.instructions[3].durationMinutes)
        assertEquals("BUS", journey.instructions[3].travelMode)

        assertEquals("Walk 569m", journey.instructions[6].text)
        assertEquals(7, journey.instructions[6].durationMinutes)
        assertEquals("WALK", journey.instructions[6].travelMode)

        // Assertions for the stops
        assertEquals(3, journey.stops.size)
        assertEquals("Salt River Station", journey.stops[0].name)
        assertEquals("WALK", journey.stops[0].fromMode)
        assertEquals("HEAVY_RAIL", journey.stops[0].toMode)
        assertEquals("Southern Line", journey.stops[0].routeName)
    }

    private fun loadRouteFromJson(fileName: String): Route {
        val inputStream = this.javaClass.classLoader!!.getResourceAsStream(fileName)
        val reader = InputStreamReader(inputStream)
        // Use Gson to parse the JSON into our data class
        val response = Gson().fromJson(reader, RoutesResponse::class.java)
        reader.close()
        // Return the first route from the list
        return response.routes.first()
    }
}