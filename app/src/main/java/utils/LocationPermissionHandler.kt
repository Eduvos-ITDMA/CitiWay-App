package com.example.citiway.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Creates a handler for managing location permissions in the app.
 *
 * This handles:
 * - Checking if permission is already granted
 * - Requesting permission from the user
 * - Opening app settings if permission was permanently denied
 * - Providing callbacks to react to permission results
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissionHandler(
    onPermissionResult: (granted: Boolean) -> Unit = {}
): LocationPermissionHandler {
    val context = LocalContext.current

    // Using Accompanist library to manage multiple location permissions
    // (FINE and COARSE location)
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        // Callback when user responds to permission dialog
        onPermissionsResult = { permissions ->
            // Check if at least one location permission was granted
            val granted = permissions.values.any { it }
            onPermissionResult(granted)
        }
    )

    // Launcher for opening app settings when permission is permanently denied
    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // User returned from settings - recheck permission status
        // The permissionsState will automatically update when user comes back
        onPermissionResult(permissionsState.allPermissionsGranted)
    }

    return remember(permissionsState, context) {
        LocationPermissionHandler(
            hasPermission = permissionsState.allPermissionsGranted,
            shouldShowRationale = permissionsState.shouldShowRationale,
            requestPermission = { permissionsState.launchMultiplePermissionRequest() },
            openSettings = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                settingsLauncher.launch(intent)
            }
        )
    }
}

data class LocationPermissionHandler(
    val hasPermission: Boolean,
    val shouldShowRationale: Boolean,
    val requestPermission: () -> Unit,
    val openSettings: () -> Unit
)