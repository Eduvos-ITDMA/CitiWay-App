package com.example.citiway.core.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.features.SplashScreen
import com.example.citiway.features.help.HelpRoute
import com.example.citiway.features.progress_tracker.ProgressTrackerRoute

fun NavGraphBuilder.topLevelDestinations(
    navController: NavController,
    drawerState: DrawerState,
) {
    // Splash screen
    composable(Screen.Splash.route) { SplashScreen(navController) }

    // Progress Tracker screen
    composable(Screen.ProgressTracker.route) {
        ProgressTrackerRoute(navController, drawerState)
    }

    // Help screen
    composable(Screen.Help.route) {
        HelpRoute(navController, drawerState)
    }
}
