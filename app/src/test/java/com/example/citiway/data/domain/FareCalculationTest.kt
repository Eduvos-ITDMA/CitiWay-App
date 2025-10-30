package com.example.citiway.data.domain

import com.example.citiway.data.local.entities.MyCitiFare
import com.example.citiway.data.remote.Route
import com.example.citiway.data.remote.Step
import com.example.citiway.data.remote.Vehicle
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.features.shared.loadRoutesFromJson
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FareCalculationTest {
    private lateinit var mycitiBusService: MycitiBusService
    private lateinit var metrorailService: MetrorailService
    private lateinit var mockRoutes: List<Route>
    private lateinit var repository: CitiWayRepository

    @Before
    fun setUp() {
        mockRoutes = loadRoutesFromJson("sample_routes_response.json", this.javaClass.classLoader!!)
        repository = mockk(relaxed = true)

        coEvery { repository.getMyCitiFare(any()) } answers {
            getMycitiFare(firstArg())
        }
    }

    @Test
    fun `your first test case here`() = runTest {
        val results = mutableListOf<FareBreakdown>()

        mockRoutes.map { route ->
            val steps = route.legs.firstOrNull()?.steps ?: emptyList()

            mycitiBusService = MycitiBusService(repository)
            metrorailService = MetrorailService()

            steps.forEach { step ->
                if (step.travelMode == "TRANSIT") {
                    when (getVehicle(step)?.type?.uppercase()) {
                        "BUS" -> mycitiBusService.adjustFare(step)
                        "HEAVY_RAIL", "RAIL" -> metrorailService.adjustFare(step)
                    }
                }
            }

            val mycitiFare = mycitiBusService.getFare()
            val metrorailFare = metrorailService.getFare()
            val fareBreakdown = FareBreakdown(
                fareTotal = mycitiFare + metrorailFare,
                metrorailFare = metrorailFare,
                mycitiFare = mycitiFare
            )

            results.add(fareBreakdown)
        }

        assertEquals(10.00, results[0].metrorailFare)
        assertEquals(18.50, results[0].mycitiFare)
        assertEquals(28.50, results[0].fareTotal)

        assertEquals(10.00, results[0].metrorailFare)
        assertEquals(18.50, results[0].mycitiFare)
        assertEquals(28.50, results[0].fareTotal)

        assertEquals(10.00, results[0].metrorailFare)
        assertEquals(18.50, results[0].mycitiFare)
        assertEquals(28.50, results[0].fareTotal)

        assertEquals(10.00, results[0].metrorailFare)
        assertEquals(18.50, results[0].mycitiFare)
        assertEquals(28.50, results[0].fareTotal)

        assertEquals(10.00, results[0].metrorailFare)
        assertEquals(18.50, results[0].mycitiFare)
        assertEquals(28.50, results[0].fareTotal)
    }

    data class FareBreakdown(
        val fareTotal: Double,
        val metrorailFare: Double,
        val mycitiFare: Double,
    )

    private fun getVehicle(step: Step?): Vehicle? {
        return step?.transitDetails?.transitLine?.vehicle
    }

    private fun getMycitiFare(distanceMeters: Int): MyCitiFare? {
        val fare = when (distanceMeters) {
            in 0..5000 -> MyCitiFare(peak_fare = 13.50, offpeak_fare = 10.50)
            in 5001..10000 -> MyCitiFare(peak_fare = 18.50, offpeak_fare = 13.50)
            in 10001..20000 -> MyCitiFare(peak_fare = 23.50, offpeak_fare = 18.50)
            in 20001..30000 -> MyCitiFare(peak_fare = 25.50, offpeak_fare = 21.50)
            in 30001..40000 -> MyCitiFare(peak_fare = 27.50, offpeak_fare = 23.50)
            in 40001..50000 -> MyCitiFare(peak_fare = 31.50, offpeak_fare = 28.50)
            in 50001..60000 -> MyCitiFare(peak_fare = 36.50, offpeak_fare = 31.50)
            in 60000..Int.MAX_VALUE -> MyCitiFare(peak_fare = 39.50, offpeak_fare = 33.50)
            else -> null
        }

        return fare
    }
}
