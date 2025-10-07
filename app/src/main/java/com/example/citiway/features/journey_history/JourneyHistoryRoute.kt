package com.example.citiway.features.journey_history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.features.shared.CompletedJourneysViewModel

@Composable
fun JourneyHistoryRoute(
    navController: NavController,
    completedJourneysViewModel: CompletedJourneysViewModel = viewModel()
) {
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()

    ScreenWrapper(navController, showBottomBar = true, content = { paddingValues ->
        JourneyHistoryContent(
            journeys = completedJourneysState.recentJourneys,
            paddingValues = paddingValues
        )
    })
}