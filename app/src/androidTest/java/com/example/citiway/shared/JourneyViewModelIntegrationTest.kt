package com.example.citiway.shared

import android.util.Log
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.example.citiway.BuildConfig
import com.example.citiway.core.utils.getNearestHalfHour
import com.example.citiway.core.utils.provideOkHttpClient
import com.example.citiway.data.remote.RoutesManager
import com.example.citiway.data.remote.RoutesService
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.features.shared.JourneyViewModel
import com.google.android.gms.maps.model.LatLng
import org.junit.jupiter.api.Assertions.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_ROUTES_URL = "https://routes.googleapis.com/"

class JourneyViewModelIntegrationTest {

    @get:Rule
    val coroutineRule = CoroutineDispatcherRule()

    private lateinit var navController: TestNavHostController
    private lateinit var mockRoutesService: RoutesService
    private lateinit var mockRoutesManager: RoutesManager
    private lateinit var okHttpClient: OkHttpClient

    // The class we are testing
    private lateinit var viewModel: JourneyViewModel

    // This function runs before each test
    @Before
    fun setUp() {
        // Create mock versions of the dependencies
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        okHttpClient = provideOkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_ROUTES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        mockRoutesService = retrofit.create(RoutesService::class.java)
        mockRoutesManager = RoutesManager(BuildConfig.MAPS_API_KEY, mockRoutesService)

        // Initialize the ViewModel with the mock dependencies
        viewModel = JourneyViewModel(navController, mockRoutesManager)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getJourneyOptionsTest() = runTest {
        // ARRANGE
        val mockStartLocation = SelectedLocation(
            placeId = "mock-id-1",
            primaryText = "Mock Location 1",
            latLng = LatLng(-33.964968, 18.478063)
        )

        val mockDestination = SelectedLocation(
            placeId = "mock-id-2",
            primaryText = "Mock Location 2",
            latLng = LatLng(-33.863346, 18.522339)
        )

        viewModel.setStartLocation(mockStartLocation)
        viewModel.setDestination(mockDestination)
        viewModel.setTime(getNearestHalfHour())

        // ACT
        try {
            viewModel.getJourneyOptions()
            advanceUntilIdle()
        } catch (e: Exception) {
            // If the API call fails, this will catch the exception
            Log.e("JourneyViewModelTest", "API call failed in test", e)
            fail("The getJourneyOptions call threw an exception: ${e.message}")
        }

        // ASSERT
        val journeyOptions = viewModel.state.value.journeyOptions
        assertNotNull(journeyOptions)
        Log.d("getJourneyOptions Result", journeyOptions.toString())
    }
}