package com.example.citiway.features.journey_history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.citiway.data.local.CitiWayDatabase
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel

@Composable
fun JourneyHistoryRoute(
    navController: NavController,
   // completedJourneysViewModel: CompletedJourneysViewModel = viewModel()
) {
    // Now just creating it where we need it, less gymnastics
    val database = CitiWayDatabase.getDatabase(LocalContext.current)
    val completedJourneysViewModel: CompletedJourneysViewModel = viewModel(
        factory = viewModelFactory {
            CompletedJourneysViewModel(database.savedPlaceDao())
        }
    )
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()

    ScreenWrapper(navController, showBottomBar = true, content = { paddingValues ->
        JourneyHistoryContent(
            journeys = completedJourneysState.recentJourneys,
            paddingValues = paddingValues
        )
    })
}