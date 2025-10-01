package com.example.citiway.features.start_location_selection

import android.Manifest
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartLocationSelectionRoute(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: StartLocationSelectionViewModel = viewModel()
) {
    val state by viewModel.screenState.collectAsState()
    val actions = viewModel.actions

    /*
     * Create a permission state that automatically updates when the permission status changes
     * This ensures we automatically get location as soon as user grants permission.
     */
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted) {
            actions.getCurrentLocation()
        }
    }

    ScreenWrapper(navController, drawerState, true) { paddingValues ->
        StartLocationSelectionContent(
            paddingValues = paddingValues,
            state = state,
            actions = actions,
            onPermissionRequest = { locationPermissionState.launchPermissionRequest() },
            navController,
        )
    }
}
