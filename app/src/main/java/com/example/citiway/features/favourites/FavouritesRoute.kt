package com.example.citiway.features.favourites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel

/**
 * Route composable for the Favourites screen
 *
 * Architecture Flow: Route → ViewModel → Repository → DAO → Database
 *
 * This route handles:
 * - Manual Dependency Injection: Constructs Database → Repository → ViewModel chain
 * - ViewModel Factory: Injects Repository into ViewModel for database operations
 * - Lifecycle-aware State: Collects state that pauses when screen is backgrounded
 * - Favourite Toggle: Passes toggle function to allow marking/unmarking favourites
 *
 * Each screen creates its own ViewModel instance locally, avoiding parameter passing
 * complexity while maintaining proper lifecycle scoping.
 */
@Composable
fun FavouritesRoute(
    navController: NavController
) {
    // ViewModel with factory for constructor injection
    val completedJourneysViewModel: CompletedJourneysViewModel = viewModel(
        factory = viewModelFactory {
            CompletedJourneysViewModel()
        }
    )

    // Lifecycle-aware state collection
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()

    // Render screen with bottom bar
    ScreenWrapper(navController, showBottomBar = true, content = { paddingValues ->
        FavouritesContent(
            journeys = completedJourneysState.favouriteJourneys,
            paddingValues = paddingValues,
            onToggleFavourite = completedJourneysViewModel::toggleFavourite
        )
    })
}