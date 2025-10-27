package com.example.citiway.core.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.features.help.HelpRoute
import com.example.citiway.features.onboarding.OnboardingRoute
import com.example.citiway.features.progress_tracker.ProgressTrackerRoute

fun NavGraphBuilder.topLevelDestinations(
    navController: NavController,
) {

    composable(route = Screen.Onboarding.route) {
        OnboardingRoute(navController)
    }

    // Progress Tracker screen
    composable(Screen.ProgressTracker.route) {
        ProgressTrackerRoute(navController)
    }

    // Help screen
    composable(Screen.Help.route) {
        HelpRoute(navController)
    }
}
