package com.example.citiway.features.start_location_selection

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.ui.components.LocationPermissionDialog
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.DrawerViewModel
import com.example.citiway.features.shared.JourneyViewModel
import com.example.citiway.features.shared.LocationType
import com.example.citiway.features.shared.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartLocationSelectionRoute(
    navController: NavController, placesManager: PlacesManager = App.appModule.placesManager
) {
    val context = LocalActivity.current as ComponentActivity
    // Getting activity-scoped DrawerViewModel
    val drawerViewModel: DrawerViewModel = viewModel(
        viewModelStoreOwner = context
    )
    val locationEnabledInApp by drawerViewModel.locationEnabled.collectAsState()

    val mapViewModel: MapViewModel = viewModel(
        factory = viewModelFactory {
            MapViewModel(placesManager)
        })

    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = context,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        })

    // MapViewModel state and actions
    val mapState by mapViewModel.screenState.collectAsStateWithLifecycle()
    val mapActions = mapViewModel.actions

    // Track variables for determining device location usage
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var permissionJustRequested by remember { mutableStateOf(false) }
    var showPermissionMismatchDialog by remember { mutableStateOf(false) }

    // DUAL CHECK: Monitoring BOTH system permission AND DataStore preference (in-app toggle)
    LaunchedEffect(locationPermissionState.status.isGranted, locationEnabledInApp) {
        val systemGranted = locationPermissionState.status.isGranted

        // Syncing DataStore only when user actively granted permission via our button
        // This prevents overriding if user intentionally disabled location in app settings
        if (systemGranted && permissionJustRequested && !locationEnabledInApp) {
            drawerViewModel.toggleLocation(true)
            permissionJustRequested = false
        }

        // Fetching location only when BOTH conditions are true
        if (systemGranted && locationEnabledInApp) {
            placesManager.useUserLocation()
            navController.navigate(Screen.JourneySelection.route)
        } else if (locationEnabledInApp) {
            showPermissionMismatchDialog = true
        }
    }

    // Showing dialog when permissions are mismatched
    if (showPermissionMismatchDialog) {
        LocationPermissionDialog(
            onDismiss = { showPermissionMismatchDialog = false },
            onEnableNow = {
                locationPermissionState.launchPermissionRequest()
                showPermissionMismatchDialog = false
            },
            onEnableLater = {
                drawerViewModel.toggleLocation(false)
                showPermissionMismatchDialog = false
            })
    }

    // Navigating to journey selection once location is confirmed
    val onConfirmLocation: (SelectedLocation) -> Unit = { location ->
        journeyViewModel.confirmLocationSelection(location, LocationType.START, mapViewModel::clearSearch)
    }

    ScreenWrapper(navController, true, { paddingValues ->
        StartLocationSelectionContent(
            paddingValues = paddingValues,
            state = mapState,
            actions = mapActions,
            onPermissionRequest = {
                // Marking that user initiated the request so we can distinguish between:
                // 1. User actively clicking "Use my location" (should sync DataStore to true)
                // 2. System permission existing from before but app setting disabled (should NOT override user's choice)
                // This prevents us from automatically re-enabling location if user intentionally disabled it in app settings. No lies to user.
                permissionJustRequested = true
                locationPermissionState.launchPermissionRequest()
            },
            cameraPositionState = mapViewModel.cameraPositionState,
            onConfirmLocation = onConfirmLocation,
            locationEnabledInApp = locationEnabledInApp
        )
    })
}