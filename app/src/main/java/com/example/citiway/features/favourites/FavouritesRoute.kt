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
 */

@Composable
fun FavouritesRoute(
    navController: NavController
) {
    val placesActions = App.appModule.placesManagerFactory.create().actions

    // ViewModels with factory for constructor injection
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
        })

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