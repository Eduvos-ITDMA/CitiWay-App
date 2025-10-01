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

@Composable
fun Drawer(
    drawerState: DrawerState,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        DrawerContent()
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
private fun DrawerContent() {
    var locationEnabled by remember { mutableStateOf(true) }
    var myCitiEnabled by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }

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
            TextButton(onClick = { /* Handle go back */ }) {
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
            onClick = { /* Handle click */ }
        )

        HorizontalDivider()

        // Location with toggle
        DrawerMenuItemWithSwitch(
            title = "Location",
            subtitle = "Turn Location on",
            checked = locationEnabled,
            onCheckedChange = { locationEnabled = it }
        )

        HorizontalDivider()

        // MyCiTi Connection with toggle
        DrawerMenuItemWithSwitch(
            title = "MyCiTi Connection",
            subtitle = "Card Member\nAccess discounted pricing\nwhile utilizing MyCiTi services",
            checked = myCitiEnabled,
            onCheckedChange = { myCitiEnabled = it }
        )

        HorizontalDivider()

        // Help & FAQ
        DrawerMenuItem(
            title = "Help & FAQ",
            subtitle = "Access Our Helpline",
            onClick = { /* Handle click */ }
        )

        HorizontalDivider()

        // Theme with toggle
        DrawerMenuItemWithSwitch(
            title = "Theme",
            subtitle = "Switch to Dark/Light Mode",
            checked = darkModeEnabled,
            onCheckedChange = { darkModeEnabled = it }
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