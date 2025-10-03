package com.example.citiway.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * TopBar Component
 *
 * Composable function rendering the application's top navigation bar with back navigation
 * and menu access. The component manages navigation state and menu visibility while
 * maintaining a consistent Material Design 3 appearance.
 *
 * Key Features:
 * - Dynamic back button that's only enabled when navigation stack allows popping
 * - Hamburger menu icon triggering an overlay settings menu
 * - Center-aligned design following Material 3 guidelines
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Managing menu expansion state - initially collapsed
    var menuExpanded by remember { mutableStateOf(false) }

    // Checking if back navigation is available by inspecting the navigation stack
    val canPop = navController.previousBackStackEntry != null

    // Using Box to layer the menu on top of the app bar
    Box {
        CenterAlignedTopAppBar(
            title = {}, // Empty title - keeping the design minimal
            modifier = modifier,

            // Leading icon: Back button
            // Only rendering when there's a previous screen to navigate to
            navigationIcon = {
                if (canPop) {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },

            // Trailing icon: Menu button
            // Opening the settings menu overlay on click
            actions = {
                IconButton(
                    onClick = { menuExpanded = true }, // Toggling menu visibility
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Open Menu",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },

            // Applying Material Theme surface color for consistent theming **Just temporary colors, to be refined
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),

            // Keeping the app bar pinned (not scrolling away with content)
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        )

        // Rendering the settings menu as an overlay
        // This component appears on top of the app bar when expanded
        ModernSettingsMenu(
            expanded = menuExpanded,
            onDismiss = { menuExpanded = false }, // Collapsing menu on dismiss
            navController = navController
        )
    }
}