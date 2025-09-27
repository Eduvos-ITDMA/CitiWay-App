package com.example.citiway.ui.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.citiway.ui.navigation.routes.Screen
import com.example.citiway.ui.navigation.components.ScreenWrapper
import com.example.citiway.ui.screens.HelpScreen
import com.example.citiway.ui.screens.ProgressTrackerScreen
import com.example.citiway.ui.screens.SplashScreen

fun NavGraphBuilder.topLevelDestinations(
    navController: NavController,
    drawerState: DrawerState,
) {
    // Splash screen
    composable(Screen.Splash.route) { SplashScreen(navController) }

    // Progress Tracker screen
    composable(Screen.ProgressTracker.route) {
        ScreenWrapper(
            navController, drawerState, true
        ) { navController, paddingValues ->
            ProgressTrackerScreen(navController, paddingValues)
        }
    }

    // Help screen
    composable(Screen.Help.route) {
        ScreenWrapper(navController, drawerState, true) { navController, paddingValues ->
            HelpScreen(navController, paddingValues)
        }
    }
}
