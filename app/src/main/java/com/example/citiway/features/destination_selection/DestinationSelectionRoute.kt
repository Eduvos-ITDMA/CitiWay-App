package com.example.citiway.features.destination_selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.util.ScreenWrapper
import com.example.citiway.features.shared.LocationSelectionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DestinationSelectionRoute(
    navController: NavController,
    viewModel: LocationSelectionViewModel = viewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val actions = viewModel.actions

    val onConfirmLocation: (LatLng) -> Unit = { location ->
        // TODO: store selected location in shared view model
        navController.navigate(Screen.StartLocationSelection.route)
    }

    ScreenWrapper(navController, showBottomBar = true) { paddingValues ->
        DestinationSelectionContent(
            paddingValues = paddingValues,
            state = state,
            actions = actions,
            cameraPositionState = viewModel.cameraPositionState,
            onConfirmLocation = onConfirmLocation
        )
    }
}
