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
    val context = LocalActivity.current as ComponentActivity

    val mapViewModel: MapViewModel = viewModel(
        factory = viewModelFactory {
            MapViewModel(App.appModule.placesManager)
        }
    )

    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = context,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        })

    val state by mapViewModel.screenState.collectAsStateWithLifecycle()
    val actions = mapViewModel.actions

    // Navigating to start location selection screen once location is confirmed
    val onConfirmLocation: (SelectedLocation) -> Unit = { location ->
        journeyViewModel.confirmLocationSelection(
            location,
            LocationType.START,
            mapViewModel::clearSearch
        )
    }

    ScreenWrapper(navController, showBottomBar = true) { paddingValues ->
        DestinationSelectionContent(
            paddingValues = paddingValues,
            state = state,
            actions = actions,
            cameraPositionState = mapViewModel.cameraPositionState,
            onConfirmLocation = onConfirmLocation
        )
    }
}
