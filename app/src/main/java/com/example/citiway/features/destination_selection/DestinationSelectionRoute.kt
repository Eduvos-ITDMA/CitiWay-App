package com.example.citiway.features.destination_selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.features.shared.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng
import com.example.citiway.App
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.LocationType

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DestinationSelectionRoute(
    navController: NavController,
) {
    val mapViewModel: MapViewModel = viewModel(
        factory = viewModelFactory {
            MapViewModel(App.appModule.placesManager, LocationType.END)
        }
    )

    val state by mapViewModel.screenState.collectAsStateWithLifecycle()
    val actions = mapViewModel.actions

    val onConfirmLocation: (LatLng) -> Unit = { location ->
        navController.navigate(Screen.StartLocationSelection.route)
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
