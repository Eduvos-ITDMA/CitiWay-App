package com.example.citiway.features.journey_history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.citiway.data.local.CitiWayDatabase
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel

@Composable
fun JourneyHistoryRoute(
    navController: NavController,
   // completedJourneysViewModel: CompletedJourneysViewModel = viewModel()
) {
    // Now just creating it where we need it, less gymnastics
    // Get database and repository
    val context = LocalContext.current
    val database = CitiWayDatabase.getDatabase(context)
    val repository = CitiWayRepository(database)

    // Use YOUR existing viewModelFactory helper! 🎉
    val completedJourneysViewModel: CompletedJourneysViewModel = viewModel(
        factory = viewModelFactory {
            CompletedJourneysViewModel(repository = repository)
        }
    )


    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()

    ScreenWrapper(navController, showBottomBar = true, content = { paddingValues ->
        JourneyHistoryContent(
            journeys = completedJourneysState.allJourneys,
            paddingValues = paddingValues
        )
    })
}