package com.example.citiway.features.progress_tracker

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.data.local.CompletedJourney
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.Journey
import com.example.citiway.features.shared.JourneyViewModel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProgressTrackerRoute(
    navController: NavController,
) {
    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        }
    )
    val journeyState by journeyViewModel.state.collectAsStateWithLifecycle()

    val repository: CitiWayRepository = App.appModule.repository

    val onJourneyComplete: (SelectedLocation, SelectedLocation, Journey) -> Unit =
        { startLocation, destination, journey ->
            journeyViewModel.viewModelScope.launch {
                try {
                    val completedJourney = CompletedJourney(
                        userId = 1,
                        startLocationName = startLocation.primaryText,
                        destinationName = destination.primaryText,
                        startLocationLatLng = startLocation.latLng,
                        destinationLatLng = destination.latLng,
                        journey = journey,
                        createdAt = System.currentTimeMillis()
                    )

                    val journeyId = repository.insertCompletedJourney(completedJourney)
                    Log.d(
                        "JourneyViewModel",
                        "🎉 Journey saved! Trip ID: $journeyId"
                    )

                } catch (e: Exception) {
                    Log.e("JourneyViewModel", "❌ Failed to save journey: ${e.message}", e)
                    e.printStackTrace()
                }
            }
        }

    ScreenWrapper(navController, true) { paddingValues ->
        ProgressTrackerContent(
            journeyState = journeyState,
            paddingValues = paddingValues,
            navController = navController,
            journeyViewModel::toggleProgressSpeedUp,
            onJourneyComplete = onJourneyComplete
        )
    }
}