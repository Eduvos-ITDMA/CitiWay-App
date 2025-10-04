package com.example.citiway.features.start_location_selection

import android.Manifest
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.util.ScreenWrapper
import com.example.citiway.features.shared.DrawerViewModel
import com.example.citiway.features.shared.LocationSelectionViewModel
import com.example.citiway.features.shared.LocationSelectionViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartLocationSelectionRoute(
    navController: NavController
) {


    // Create LocationSelectionViewModel with factory, passing the drawerViewModel
    val context = LocalContext.current
    // Get activity-scoped DrawerViewModel (shared across all screens)
    val drawerViewModel: DrawerViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity
    )
    // ADD THIS LINE - collect the locationEnabled state from DrawerViewModel
    val locationEnabledInApp by drawerViewModel.locationEnabled.collectAsState()
    val viewModel: LocationSelectionViewModel = viewModel(
        factory = LocationSelectionViewModelFactory(
            application = context.applicationContext as Application,
            drawerViewModel = drawerViewModel
        )
    )

    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val actions = viewModel.actions

    // Track system-level permission
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // DUAL CHECK: Monitor BOTH system permission AND DataStore preference
    LaunchedEffect(locationPermissionState.status.isGranted, locationEnabledInApp) {
        val systemGranted = locationPermissionState.status.isGranted

        // Update ViewModel with system permission status
        actions.onLocationPermissionsStatusChanged(systemGranted)

        // Only fetch location if BOTH conditions are true
        if (systemGranted && locationEnabledInApp) {
            actions.getCurrentLocation()
        }
    }

    // Show dialog if mismatch: app enabled but system denied
    var showPermissionMismatchDialog by remember { mutableStateOf(false) }

    LaunchedEffect(locationEnabledInApp, locationPermissionState.status.isGranted) {
        if (locationEnabledInApp && !locationPermissionState.status.isGranted) {
            showPermissionMismatchDialog = true
        }
    }

    if (showPermissionMismatchDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionMismatchDialog = false },
            title = { Text("Location Permission Needed") },
            text = {
                Text("Location is enabled in app settings, but system permission is denied. Grant permission to use location features.")
            },
            confirmButton = {
                TextButton(onClick = {
                    locationPermissionState.launchPermissionRequest()
                    showPermissionMismatchDialog = false
                }) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // Turn off DataStore preference to match system state
                    drawerViewModel.toggleLocation(false)
                    showPermissionMismatchDialog = false
                }) {
                    Text("Keep Disabled")
                }
            }
        )
    }

    val onConfirmLocation: (LatLng) -> Unit = { location ->
        navController.navigate(Screen.JourneySelection.route)
    }

    ScreenWrapper(navController, true, { paddingValues ->
        StartLocationSelectionContent(
            paddingValues = paddingValues,
            state = state,
            actions = actions,
            onPermissionRequest = { locationPermissionState.launchPermissionRequest() },
            cameraPositionState = viewModel.cameraPositionState,
            onConfirmLocation = onConfirmLocation
        )
    })
}