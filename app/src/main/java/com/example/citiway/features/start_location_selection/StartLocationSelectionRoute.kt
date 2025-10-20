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
import com.example.citiway.core.ui.components.LocationPermissionDialog
import com.example.citiway.core.utils.ScreenWrapper
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
    navController: NavController,
) {
    val placesManager = App.appModule.placesManager
    val placesState by placesManager.state.collectAsStateWithLifecycle()
    val placesActions = placesManager.actions

    val context = LocalActivity.current as ComponentActivity

    // Getting activity-scoped DrawerViewModel
    val drawerViewModel: DrawerViewModel = viewModel(
        viewModelStoreOwner = context
    )
    val locationEnabledInApp by drawerViewModel.locationEnabled.collectAsState()

    val mapViewModel: MapViewModel = viewModel()

    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = context, factory = viewModelFactory {
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

        // Aded fix Update MapViewModel with current permission state
        mapActions.updateLocationPermission(systemGranted)

        // Syncing DataStore only when user actively granted permission via our button
        // This prevents overriding if user intentionally disabled location in app settings
        if (systemGranted && permissionJustRequested && !locationEnabledInApp) {
            drawerViewModel.toggleLocation(true)
            permissionJustRequested = false
        }

        // Fetching location only when BOTH conditions are true
        if (systemGranted && locationEnabledInApp) {
            placesActions.onUseUserLocation()
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
        journeyViewModel.confirmLocationSelection(
            location, LocationType.START, placesActions.onClearSearch
        )
    }

    // In StartLocationSelectionRoute, update the ScreenWrapper call:

    ScreenWrapper(navController, true, { paddingValues ->
        StartLocationSelectionContent(
            paddingValues = paddingValues,
            state = mapState,
            actions = mapActions,
            placesState = placesState,
            placesActions = placesActions,
            onPermissionRequest = {
                // This old handler is now replaced by smart logic in the content
                // But keep it for backward compatibility
                permissionJustRequested = true
                locationPermissionState.launchPermissionRequest()
            },
            cameraPositionState = mapViewModel.cameraPositionState,
            onConfirmLocation = onConfirmLocation,
            locationEnabledInApp = locationEnabledInApp,
            // ADDED THESE NEW PARAMETERS:
            isLocationPermissionGranted = locationPermissionState.status.isGranted,
            onEnableLocation = { drawerViewModel.toggleLocation(true) },
            onRequestSystemPermission = {
                permissionJustRequested = true
                locationPermissionState.launchPermissionRequest()
            }
        )
    })
}