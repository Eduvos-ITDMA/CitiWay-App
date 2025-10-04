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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartLocationSelectionRoute(
    navController: NavController
) {


// Creating LocationSelectionViewModel with factory, passing the drawerViewModel
    val context = LocalContext.current

    // Getting activity-scoped DrawerViewModel (shared across all screens)
    val drawerViewModel: DrawerViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity
    )

    // Collecting the locationEnabled state from DrawerViewModel
    val locationEnabledInApp by drawerViewModel.locationEnabled.collectAsState()

    // Setting up LocationSelectionViewModel with application context
    val viewModel: LocationSelectionViewModel = viewModel(
        factory = LocationSelectionViewModelFactory(
            application = context.applicationContext as Application,
            drawerViewModel = drawerViewModel
        )
    )

    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val actions = viewModel.actions

    // Tracking system-level location permission
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Tracking whether user just clicked "Use my location" button
    var permissionJustRequested by remember { mutableStateOf(false) }

    // DUAL CHECK: Monitoring BOTH system permission AND DataStore preference
    LaunchedEffect(locationPermissionState.status.isGranted, locationEnabledInApp) {
        val systemGranted = locationPermissionState.status.isGranted

        // Updating ViewModel with current system permission status
        actions.onLocationPermissionsStatusChanged(systemGranted)

        // Syncing DataStore only when user actively granted permission via our button
        // This prevents overriding if user intentionally disabled location in app settings
        if (systemGranted && permissionJustRequested && !locationEnabledInApp) {
            drawerViewModel.toggleLocation(true)
            permissionJustRequested = false
        }

        // Fetching location only when BOTH conditions are true
        if (systemGranted && locationEnabledInApp) {
            actions.getCurrentLocation()
        }
    }

    // Detecting mismatch: app enabled but system permission denied
    var showPermissionMismatchDialog by remember { mutableStateOf(false) }

    LaunchedEffect(locationEnabledInApp, locationPermissionState.status.isGranted) {
        if (locationEnabledInApp && !locationPermissionState.status.isGranted) {
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
                // Turning off DataStore preference to match system state
                drawerViewModel.toggleLocation(false)
                showPermissionMismatchDialog = false
            }
        )
    }

    // Navigating to journey selection once location is confirmed
    val onConfirmLocation: (LatLng) -> Unit = { location ->
        navController.navigate(Screen.JourneySelection.route)
    }

    ScreenWrapper(navController, true, { paddingValues ->
        StartLocationSelectionContent(
            paddingValues = paddingValues,
            state = state,
            actions = actions,
            onPermissionRequest = {
                // Marking that user initiated the request so we can distinguish between:
                // 1. User actively clicking "Use my location" (should sync DataStore to true)
                // 2. System permission existing from before but app setting disabled (should NOT override user's choice)
                // This prevents us from automatically re-enabling location if user intentionally disabled it in app settings. No lies to user.
                permissionJustRequested = true
                locationPermissionState.launchPermissionRequest()
            },
            cameraPositionState = viewModel.cameraPositionState,
            onConfirmLocation = onConfirmLocation,
            locationEnabledInApp = locationEnabledInApp
        )
    })
}

@Composable
private fun LocationPermissionDialog(
    onDismiss: () -> Unit,
    onEnableNow: () -> Unit,
    onEnableLater: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Location Access",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Explaining why we need location access
                Text(
                    text = "CitiWay uses your location to help you find routes and navigate.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Text(
                    text = "Your Options:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // Showing user they can enable now for immediate access
                OptionItem(
                    icon = Icons.Default.Check,
                    text = "Enable location access now for the best experience"
                )

                // Letting user know they can enable from device settings later
                OptionItem(
                    icon = Icons.Default.Settings,
                    text = "Enable it later from your device settings"
                )

                // Informing user about in-app toggle control
                OptionItem(
                    icon = Icons.Default.Lock,
                    text = "Toggle location usage anytime in the app settings"
                )

                // Reassuring user they have full control over permissions
                Text(
                    text = "You can always revoke location permissions from your device settings or disable location usage within the app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            // Primary action - requesting system permission now
            Button(
                onClick = onEnableNow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enable Now")
            }
        },
        dismissButton = {
            // Secondary action - dismissing and turning off app setting to match system state
            TextButton(
                onClick = onEnableLater,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Maybe Later")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun OptionItem(
    icon: ImageVector,
    text: String
) {
    // Displaying individual option with icon and descriptive text
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}