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
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.di.viewModelFactory
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
    val onJourneyComplete: () -> Unit = {
        journeyViewModel.viewModelScope.launch {
            val journeyId = journeyViewModel.onJourneyComplete()
            if (journeyId == null){
                Log.e("JourneyViewModel", "Error occurred saving completed journey")
            } else {
                navController.navigate(Screen.JourneySummary.createRoute(journeyId, "home"))
            }
        }
    }

    ScreenWrapper(navController, true) { paddingValues ->
        ProgressTrackerContent(
            journeyState = journeyState,
            paddingValues = paddingValues,
            navController = navController,
            toggleSpeedUp = journeyViewModel::toggleProgressSpeedUp,
            onJourneyComplete = onJourneyComplete,
        )
    }
}