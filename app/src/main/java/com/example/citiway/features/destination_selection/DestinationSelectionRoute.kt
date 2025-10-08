package com.example.citiway.features.destination_selection

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.JourneyViewModel
import com.example.citiway.features.shared.LocationType
import com.example.citiway.features.shared.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DestinationSelectionRoute(
    navController: NavController,
) {
    val placesManager = App.appModule.placesManager
    val placesState by placesManager.state.collectAsStateWithLifecycle()
    val placesActions = placesManager.actions

    val context = LocalActivity.current as ComponentActivity

    val mapViewModel: MapViewModel = viewModel()

    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = context, factory = viewModelFactory {
            JourneyViewModel(navController)
        })

    val state by mapViewModel.screenState.collectAsStateWithLifecycle()
    val actions = mapViewModel.actions

    // Navigating to start location selection screen once location is confirmed
    val onConfirmLocation: (SelectedLocation) -> Unit = { location ->
        journeyViewModel.confirmLocationSelection(
            location, LocationType.END, placesActions.onClearSearch
        )
    }

    ScreenWrapper(navController, showBottomBar = true) { paddingValues ->
        DestinationSelectionContent(
            paddingValues = paddingValues,
            state = state,
            actions = actions,
            placesState = placesState,
            placesActions = placesActions,
            cameraPositionState = mapViewModel.cameraPositionState,
            onConfirmLocation = onConfirmLocation
        )
    }
}
