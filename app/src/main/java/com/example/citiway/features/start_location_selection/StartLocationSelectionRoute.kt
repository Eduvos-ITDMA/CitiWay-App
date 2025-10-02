package com.example.citiway.features.start_location_selection

import android.Manifest
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.util.ScreenWrapper
import com.example.citiway.features.shared.LocationSelectionViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartLocationSelectionRoute(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: LocationSelectionViewModel = viewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val actions = viewModel.actions

    /*
     * Create a permission state that automatically updates when the permission status changes
     * This ensures we automatically get location as soon as user grants permission.
     */
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(locationPermissionState.status.isGranted) {
        actions.onLocationPermissionsStatusChanged(locationPermissionState.status.isGranted)

        // If location permission granted, immediately fetch current location
        if (locationPermissionState.status.isGranted) {
            actions.getCurrentLocation()
        }
    }

    val onConfirmLocation: (LatLng) -> Unit = { location ->
        // TODO: store selected location in shared view model instance (singleton with Hilt)
        navController.navigate(Screen.JourneySelection.route)
    }

    ScreenWrapper(navController, drawerState, true) { paddingValues ->
        StartLocationSelectionContent(
            paddingValues = paddingValues,
            state = state,
            actions = actions,
            onPermissionRequest = { locationPermissionState.launchPermissionRequest() },
            cameraPositionState = viewModel.cameraPositionState,
            onConfirmLocation = onConfirmLocation
        )
    }
}
