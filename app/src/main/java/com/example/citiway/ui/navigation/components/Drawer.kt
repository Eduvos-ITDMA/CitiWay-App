package com.example.citiway.ui.navigation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.citiway.viewmodel.DrawerViewModel
import com.example.citiway.ui.navigation.routes.Screen
import com.example.citiway.utils.rememberLocationPermissionHandler

@Composable
fun Drawer(
    drawerState: DrawerState,
    navController: NavController,
    content: @Composable () -> Unit
) {
    val viewModel: DrawerViewModel = viewModel()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .fillMaxHeight(0.85f) // Only 85% of screen height
                        .padding(top = 48.dp) // Padding from top to avoid camera cutout
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        DrawerContent(
                            navController = navController,
                            drawerState = drawerState,
                            viewModel = viewModel
                        )
                    }
                }
            },
            gesturesEnabled = drawerState.isOpen,
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    content()
                }
            }
        )
    }
}

@Composable
private fun DrawerContent(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: DrawerViewModel
) {
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    val locationEnabled by viewModel.locationEnabled.collectAsState()
    val myCitiEnabled by viewModel.myCitiEnabled.collectAsState()
    val scope = rememberCoroutineScope()

    // Show dialog when permission is needed
    var showPermissionDialog by remember { mutableStateOf(false) }

    // FIXED: Create handler first, THEN use it in the callback
    val locationPermissionHandler = rememberLocationPermissionHandler { granted ->
        if (granted) {
            // Permission was granted! Update the toggle
            viewModel.toggleLocation(true)
        } else {
            // Permission denied - keep toggle off
            viewModel.toggleLocation(false)
        }
    }

    // Now you can safely check shouldShowRationale
    LaunchedEffect(locationPermissionHandler.shouldShowRationale) {
        if (locationPermissionHandler.shouldShowRationale) {
            showPermissionDialog = true
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Location Permission Required") },
            text = { Text("This app needs location permission to show nearby routes and stops. Please grant permission in settings.") },
            confirmButton = {
                TextButton(onClick = {
                    locationPermissionHandler.openSettings()
                    showPermissionDialog = false
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        // Header with Go Back
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                scope.launch {
                    drawerState.close()
                }
            }) {
                Text("Go Back")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Settings Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        HorizontalDivider()

        // Journey History
        DrawerMenuItem(
            title = "Journey History",
            subtitle = "Access Journey History",
            onClick = {
                scope.launch {
                    drawerState.close()
                }
                navController.navigate(Screen.JourneyHistory.route)
            }
        )

        HorizontalDivider()

        // Location with toggle
        DrawerMenuItemWithSwitch(
            title = "Location",
            subtitle = "Turn Location on",
            checked = locationEnabled,
            onCheckedChange = { enabled ->
                if (enabled) {
                    // User wants to enable location
                    if (locationPermissionHandler.hasPermission) {
                        // Already has permission, just save the preference
                        viewModel.toggleLocation(true)
                    } else {
                        // Need to request permission
                        if (locationPermissionHandler.shouldShowRationale) {
                            // Show explanation dialog
                            showPermissionDialog = false
                        } else {
                            // Request permission directly
                            locationPermissionHandler.requestPermission()
                        }
                    }
                } else {
                    // User wants to disable location
                    viewModel.toggleLocation(false)
                }
            }
        )

        HorizontalDivider()

        // MyCiTi Connection with toggle
        DrawerMenuItemWithSwitch(
            title = "MyCiTi Connection",
            subtitle = "Card Member\nAccess discounted pricing\nwhile utilizing MyCiTi services",
            checked = myCitiEnabled,
            onCheckedChange = { viewModel.toggleMyCiti(it) }
        )

        HorizontalDivider()

        // Help & FAQ
        DrawerMenuItem(
            title = "Help & FAQ",
            subtitle = "Access Our Helpline",
            onClick = {
                scope.launch {
                    drawerState.close()
                }
                navController.navigate(Screen.Help.route)
            }
        )

        HorizontalDivider()

        // Theme with toggle
        DrawerMenuItemWithSwitch(
            title = "Theme",
            subtitle = "Switch to Dark/Light Mode",
            checked = darkModeEnabled,
            onCheckedChange = { viewModel.toggleDarkMode(it) }
        )
    }
}

@Composable
private fun DrawerMenuItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DrawerMenuItemWithSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}