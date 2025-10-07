package com.example.citiway.features.home

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
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel
import com.example.citiway.features.shared.JourneyViewModel
import com.example.citiway.features.shared.LocationType
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.launch

data class HomeActions(
    val onToggleFavourite: (String) -> Unit,
    val onSchedulesLinkClick: () -> Unit,
    val onMapIconClick: () -> Unit,
    val onSelectPrediction: (AutocompletePrediction, PlacesManager) -> Unit,
    val onFavouritesTitleClick: () -> Unit,
    val onRecentTitleClick: () -> Unit,
)

@Composable
fun HomeRoute(
    navController: NavController,
    completedJourneysViewModel: CompletedJourneysViewModel = viewModel()
) {
    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        }
    )
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()

    val onSelectPrediction: (AutocompletePrediction, PlacesManager) -> Unit = { prediction, placesManager ->
        // Set selectedLocation using prediction
        journeyViewModel.viewModelScope.launch {
            placesManager.selectPlace(prediction)
            placesManager.selectedLocation.collect { location ->
                if (location != null) journeyViewModel.confirmLocationSelection(
                    location,
                    LocationType.START,
                    placesManager::clearSearch
                )
            }
        }
    }

    val actions = HomeActions(
        completedJourneysViewModel::toggleFavourite,
        { navController.navigate(Screen.Schedules.route) },
        { navController.navigate(Screen.DestinationSelection.route) },
        onSelectPrediction,
        onFavouritesTitleClick = {navController.navigate(Screen.Favourites.route)},
        onRecentTitleClick = {navController.navigate(Screen.JourneyHistory.route)}
    )

    ScreenWrapper(navController, true, { paddingValues ->
        HomeContent(
            completedJourneysState = completedJourneysState,
            paddingValues = paddingValues,
            actions = actions
        )
    })
}
