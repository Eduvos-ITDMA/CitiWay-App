package com.example.citiway.features.favourites

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel
import com.example.citiway.features.shared.JourneyViewModel

/**
 * Route composable for the Favourites screen
 *
 * Architecture Flow: Route → ViewModel → Repository → DAO → Database
 *
 * This route handles:
 * - Manual Dependency Injection: Constructs Database → Repository → ViewModel chain
 * - ViewModel Factory: Injects Repository into ViewModel for database operations
 * - Lifecycle-aware State: Collects state that pauses when screen is backgrounded
 * - Favorite Toggle: Passes toggle function to allow marking/unmarking favorites
 *
 * Each screen creates its own ViewModel instance locally, avoiding parameter passing
 * complexity while maintaining proper lifecycle scoping.
 */
@Composable
fun FavouritesRoute(
    navController: NavController
) {
    // ViewModel with factory for constructor injection

    val placesActions = App.appModule.placesManager.actions

    // JourneyViewModel scoped to Activity for shared navigation state
    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        })

    val completedJourneysViewModel: CompletedJourneysViewModel = viewModel(
        factory = viewModelFactory {
            CompletedJourneysViewModel(
                placesActions = placesActions,
                journeyViewModel = journeyViewModel,
                navController = navController
            )
        }
    )

    // Lifecycle-aware state collection
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()
    val completedJourneysActions = completedJourneysViewModel.actions

    // Render screen with bottom bar
    ScreenWrapper(navController, showBottomBar = true, content = { paddingValues ->
        FavouritesContent(
            journeys = completedJourneysState.favouriteJourneys,
            paddingValues = paddingValues,
            onToggleFavourite = completedJourneysViewModel::toggleFavourite,
            actions = completedJourneysActions
        )
    })
}