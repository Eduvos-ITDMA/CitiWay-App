package com.example.citiway.features.journey_history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel

/**
 * Route composable for the Journey History screen
 *
 * Architecture Flow: Route → ViewModel → Repository → DAO → Database
 *
 * This route handles:
 * - Manual Dependency Injection: Constructs Database → Repository → ViewModel chain
 *   (No Hilt/Koin used, keeping dependencies explicit and simple)
 * - ViewModel Factory: Uses viewModelFactory helper to inject Repository into ViewModel
 *   (Required for ViewModels with constructor parameters, survives configuration changes)
 * - Lifecycle-aware State: collectAsStateWithLifecycle stops collection when screen is
 *   backgrounded, saving resources while maintaining reactive UI updates
 *
 * The route acts as a bridge between navigation and UI, managing ViewModel instantiation
 * and state observation lifecycle.
 */

@Composable
fun JourneyHistoryRoute(
    navController: NavController
) {
    // Manual DI: Database → Repository → ViewModel
    val context = LocalContext.current
    val database = CitiWayDatabase.getDatabase(context)
    val repository = CitiWayRepository(database)

    // ViewModel with factory for constructor injection
    val completedJourneysViewModel: CompletedJourneysViewModel = viewModel(
        factory = viewModelFactory {
            CompletedJourneysViewModel(repository = repository)
        }
    )

    // Lifecycle-aware state collection (stops when backgrounded)
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()

    // Render screen with all journey history
    ScreenWrapper(navController, showBottomBar = true, content = { paddingValues ->
        JourneyHistoryContent(
            journeys = completedJourneysState.allJourneys, // Selecting allJourneys property from ViewModel state to render full trip history for this screen
            paddingValues = paddingValues
        )
    })
}