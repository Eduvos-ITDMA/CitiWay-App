package com.example.citiway.core.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.rememberLocationPermissionHandler
import com.example.citiway.features.shared.DrawerViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

/**
 * ModernSettingsMenu Component
 *
 * Composable function rendering an overlay settings menu that appears from the top-right
 * of the screen. Managing various app settings including location permissions, theme preferences,
 * and navigation to other screens.
 *
 * Key Features:
 * - Location permission handling with Android's security model
 * - Theme toggling between dark and light modes
 * - MyCiTi card integration for discounted pricing
 * - Navigation to Journey History and Help sections ** We can add more maybe favorites etc....
 */

@Composable
fun ModernSettingsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    navController: NavController,
) {
    val context = LocalContext.current
    val viewModel: DrawerViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity
    )
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    val locationEnabled by viewModel.locationEnabled.collectAsState()
    val myCitiEnabled by viewModel.myCitiEnabled.collectAsState()

    // Managing dialog visibility states for permission flows
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showPermissionInfoDialog by remember { mutableStateOf(false) }

    // Creating handler first, then using it in the callback
    // This handler manages location permission requests and tracks current permission status
    val locationPermissionHandler = rememberLocationPermissionHandler { granted ->
        if (granted) {
            // Permission was granted - updating the toggle to reflect active location services
            viewModel.toggleLocation(true)
        } else {
            // Permission denied - keeping toggle off to maintain consistency
            viewModel.toggleLocation(false)
        }
    }

    // Syncing stored preference with actual permission status
    // Running every time the menu opens or permission status changes
    LaunchedEffect(expanded, locationPermissionHandler.hasPermission) {
        if (expanded) {
            // If stored preference says ON but permission is actually denied
            if (locationEnabled && !locationPermissionHandler.hasPermission) {
                // Fixing the mismatch - turning off the stored preference
                viewModel.toggleLocation(false)
            }
        }
    }

    // Monitoring permission rationale state to show educational dialog
    // Android requires explaining why permission is needed after initial denial
    LaunchedEffect(locationPermissionHandler.shouldShowRationale) {
        if (locationPermissionHandler.shouldShowRationale) {
            showPermissionDialog = true
        }
    }

    // Dialog explaining why apps cannot revoke their own permissions
    // Showing users how to manually revoke permissions in system settings
    if (showPermissionInfoDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionInfoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "About Location Permission",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "For security reasons, apps cannot remove their own permissions.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Text(
                        text = "What This Means:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OptionItem(
                        icon = Icons.Default.Lock,
                        text = "Turning this off disables location features in the app"
                    )

                    OptionItem(
                        icon = Icons.Default.Check,
                        text = "The system permission will remain granted"
                    )

                    OptionItem(
                        icon = Icons.Default.Settings,
                        text = "To fully revoke, use your device settings"
                    )

                    Text(
                        text = "Path: Settings → Apps → CitiWay → Permissions → Location → Deny",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        locationPermissionHandler.openSettings()
                        showPermissionInfoDialog = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionInfoDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Got It")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Dialog appearing when permission gets blocked after 2 denials
    // Android blocks the permission popup for security after repeated denials
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Location Permission Required") },
            text = { Text("This app needs location permission to show nearby routes and stops. Please grant permission in settings.") },
            confirmButton = {
                TextButton(onClick = {
                    // Directing user to system settings to manually grant permission
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

    // Rendering custom popup aligned to top-right corner
    // Using Popup instead of DropdownMenu for better control over positioning
    if (expanded) {
        Popup(
            alignment = Alignment.TopEnd,
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true) // Enabling focus for proper dismissal behavior
        ) {
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp) // Align below top bar
                    .width(300.dp)
                    .heightIn(max = 700.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    // Header with close button integrated into the card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        // Close button replacing the hamburger icon when menu is expanded
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Menu",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Journey History navigation item
                    // Clicking navigates to journey history screen and closes menu
                    MenuItemClickable(
                        title = "Journey History",
                        subtitle = "Access Journey History",
                        onClick = {
                            onDismiss()
                            navController.navigate(Screen.JourneyHistory.route)
                        }
                    )

                    HorizontalDivider()

                    // Location toggle with complex permission handling
                    // Toggle represents "use location" not just "has permission"
                    // Can only be ON if both preference is enabled AND permission is granted
                    MenuItemWithSwitch(
                        title = "Location",
                        subtitle = if (locationEnabled && locationPermissionHandler.hasPermission) {
                            "Location services active"
                        } else if (!locationEnabled && locationPermissionHandler.hasPermission) {
                            "Location disabled in app"
                        } else {
                            "Turn Location on"
                        },
                        checked = locationEnabled && locationPermissionHandler.hasPermission,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                // User wants to enable location
                                if (locationPermissionHandler.hasPermission) {
                                    // Already has permission - just saving the preference
                                    viewModel.toggleLocation(true)
                                } else {
                                    // Need to request permission
                                    // Android blocks location request if user denies 2 times
                                    // Instead of showing popup, must manually go to settings (done for security)
                                    if (locationPermissionHandler.shouldShowRationale) {
                                        // Showing explanation dialog
                                        showPermissionDialog = true
                                    } else {
                                        // Requesting permission directly - Android's system popup will appear
                                        locationPermissionHandler.requestPermission()
                                    }
                                }
                            } else {
                                // User wants to disable location manually - clearing preference
                                viewModel.toggleLocation(false)

                                // Showing info about system permissions if they still have permission granted
                                if (locationPermissionHandler.hasPermission) {
                                    showPermissionInfoDialog = true
                                }
                            }
                        }
                    )

                    HorizontalDivider()

                    // MyCiTi card integration toggle
                    // Enabling shows discounted pricing for card members throughout the app
                    MenuItemWithSwitch(
                        title = "MyCiTi myConnect",
                        subtitle = "Card Member - Discounted pricing",
                        checked = myCitiEnabled,
                        onCheckedChange = { viewModel.toggleMyCiti(it) }
                    )

                    HorizontalDivider()

                    // Help & FAQ navigation item
                    // Clicking navigates to help screen and closes menu
                    MenuItemClickable(
                        title = "Help & FAQ",
                        subtitle = "Access Our Helpline",
                        onClick = {
                            onDismiss()
                            navController.navigate(Screen.Help.route)
                        }
                    )

                    HorizontalDivider()

                    // Theme toggle for dark/light mode switching
                    // Persisting preference across app sessions
                    MenuItemWithSwitch(
                        title = "Theme",
                        subtitle = "Switch to Dark/Light Mode",
                        checked = darkModeEnabled,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            }
        }
    }
}

/**
 * MenuItemClickable
 *
 * Reusable composable rendering a clickable menu item with title, subtitle,
 * and a trailing arrow icon. Using Surface for ripple effect on click.
 *
 * @param title Main text displayed prominently
 * @param subtitle Supporting text displayed below title in muted color
 * @param onClick Callback invoked when item is clicked
 */

@Composable
private fun MenuItemClickable(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
            // Trailing arrow indicating navigation action
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * MenuItemWithSwitch
 *
 * Reusable composable rendering a menu item with title, subtitle, and a toggle switch.
 * Used for settings that can be enabled/disabled.
 *
 * @param title Main text displayed prominently
 * @param subtitle Supporting text describing current state or toggle purpose
 * @param checked Current state of the switch
 * @param onCheckedChange Callback invoked when switch state changes
 */

@Composable
private fun MenuItemWithSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
        // Switch allowing immediate toggling of setting
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
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