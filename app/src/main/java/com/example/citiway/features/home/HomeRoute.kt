package com.example.citiway.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.util.ScreenWrapper
import com.example.citiway.features.shared.CompletedJourneysViewModel
import com.example.citiway.features.shared.LocationSelectionActions
import com.example.citiway.features.shared.LocationSelectionViewModel
import com.google.android.libraries.places.api.model.AutocompletePrediction



data class HomeActions(
    val onToggleFavourite: (String) -> Unit,
    val onSchedulesLinkClick: () -> Unit,
    val onMapIconClick: () -> Unit,
    val onSelectPrediction: (AutocompletePrediction) -> Unit,
    val locationSelectionActions: LocationSelectionActions
)

@Composable
fun HomeRoute(
    navController: NavController,
    viewModel: CompletedJourneysViewModel = viewModel(),
    completedJourneysViewModel: CompletedJourneysViewModel = viewModel(),
    locationSelectionViewModel: LocationSelectionViewModel = viewModel()
) {
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()
    val locationSelectionState by locationSelectionViewModel.screenState.collectAsStateWithLifecycle()

    val actions = HomeActions(
        completedJourneysViewModel::toggleFavourite,
        { navController.navigate(Screen.Schedules.route) },
        { navController.navigate(Screen.DestinationSelection.route) },
        { navController.navigate(Screen.StartLocationSelection.route) }, // TODO: set up journey selection flow
        locationSelectionViewModel.actions
    )

    ScreenWrapper(navController, true, { paddingValues ->
        HomeContent(
            completedJourneysState = completedJourneysState,
            locationSelectionState = locationSelectionState,
            paddingValues = paddingValues,
            actions = actions
        )
    })
}
