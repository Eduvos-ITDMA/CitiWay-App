package com.example.citiway.data.domain

import com.example.citiway.data.remote.Route
import com.example.citiway.data.remote.Step
import com.example.citiway.data.remote.Vehicle
import com.example.citiway.features.shared.loadRoutesFromJson
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FareCalculationTest {
    private lateinit var mycitiBusService: MycitiBusService
    private lateinit var metrorailService: MetrorailService
    private lateinit var mockRoutes: List<Route>

    @Before
    fun setUp() {
        mockRoutes = loadRoutesFromJson("sample_routes_response.json", this.javaClass.classLoader!!)
    }

    @Test
    fun `your first test case here`() = runTest {
        var results = mutableListOf<FareBreakdown>()

        mockRoutes.map { route ->
            val steps = route.legs.firstOrNull()?.steps ?: emptyList()

            mycitiBusService = MycitiBusService()
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
                mycitiFare + metrorailFare,
                metrorailFare,
                mycitiFare
            )

            results.add(fareBreakdown)
        }
    }

    data class FareBreakdown(
        val fareTotal: Double,
        val metrorailFare: Double,
        val mycitiFare: Double,
    )

    private fun getVehicle(step: Step?): Vehicle? {
        return step?.transitDetails?.transitLine?.vehicle
    }
}
