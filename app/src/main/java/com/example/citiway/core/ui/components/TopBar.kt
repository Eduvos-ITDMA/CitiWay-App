package com.example.citiway.core.ui.components

/**
 * This file defines the top app bar that houses the back button and the hamburger menu for opening
 * the app's drawer
 */

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController, drawerState: DrawerState, modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val canPop = navController.previousBackStackEntry != null

    CenterAlignedTopAppBar(
        title = {},
        modifier = modifier,

        // Back Button
        navigationIcon = {
            IconButton(
                onClick = {
                    if (canPop) {
                        navController.popBackStack()
                    }
                }, enabled = canPop
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
        },

        // Hamburger icon for app drawer
        actions = {
            IconButton(
                onClick = {
                    scope.launch { drawerState.open() }
                }) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Open Navigation Drawer",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}