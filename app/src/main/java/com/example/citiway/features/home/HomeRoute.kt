package com.example.citiway.features.home

import androidx.compose.material3.DrawerState
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

data class HomeActions(
    val onToggleFavourite: (String) -> Unit,
    val onSchedulesLinkClick: () -> Unit,
    val onMapIconClick: () -> Unit,
    val locationSelectionActions: LocationSelectionActions
)

@Composable
fun HomeRoute(
    navController: NavController,
    drawerState: DrawerState,
    completedJourneysViewModel: CompletedJourneysViewModel = viewModel(),
    locationSelectionViewModel: LocationSelectionViewModel = viewModel()
) {
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()
    val locationSelectionState by locationSelectionViewModel.screenState.collectAsStateWithLifecycle()

    val actions = HomeActions(
        completedJourneysViewModel::toggleFavourite,
        { navController.navigate(Screen.Schedules.route) },

        { navController.navigate(Screen.DestinationSelection.route) },
        locationSelectionViewModel.actions
    )

    ScreenWrapper(navController, drawerState, true) { paddingValues ->
        HomeContent(
            completedJourneysState = completedJourneysState,
            locationSelectionState = locationSelectionState,
            paddingValues = paddingValues,
            actions = actions
        )
    }
}
