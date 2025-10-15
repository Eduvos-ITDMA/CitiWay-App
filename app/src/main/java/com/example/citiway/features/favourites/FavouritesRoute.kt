package com.example.citiway.features.favourites

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
fun FavouritesRoute(
    navController: NavController
) {

    // ADDED THESE 3 LINES.  each screen is responsible for its own ViewModel. less gymnatics of pass viewmodel paremters.
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
        FavouritesContent(
            journeys = completedJourneysState.allFavouriteJourneys,
            paddingValues = paddingValues,
            onToggleFavourite = completedJourneysViewModel::toggleFavourite
        )
    })
}