package com.example.citiway.features.home

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.util.ScreenWrapper
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel
import com.example.citiway.features.shared.DrawerViewModel
import com.example.citiway.features.shared.LocationSelectionActions
import com.example.citiway.features.shared.MapViewModel
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
    completedJourneysViewModel: CompletedJourneysViewModel = viewModel()
) {
    val context = LocalContext.current

    // Get activity-scoped DrawerViewModel (shared across app)
    val drawerViewModel: DrawerViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity
    )

    // Create LocationSelectionViewModel with factory
    val mapViewModel = viewModel<MapViewModel>(
        viewModelStoreOwner = context,
        factory = viewModelFactory {
            MapViewModel(context.applicationContext as Application, drawerViewModel)
        }
    )

    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()
    val locationSelectionState by mapViewModel.screenState.collectAsStateWithLifecycle()

    val actions = HomeActions(
        completedJourneysViewModel::toggleFavourite,
        { navController.navigate(Screen.Schedules.route) },
        { navController.navigate(Screen.DestinationSelection.route) },
        { navController.navigate(Screen.StartLocationSelection.route) },
        mapViewModel.actions
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
