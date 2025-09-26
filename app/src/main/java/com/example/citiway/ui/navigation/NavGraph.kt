package com.example.citiway.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.citiway.ui.screens.DestinationSelectionScreen
import com.example.citiway.ui.screens.FavouritesScreen
import com.example.citiway.ui.screens.HelpScreen
import com.example.citiway.ui.screens.HomeScreen
import com.example.citiway.ui.screens.JourneyHistoryScreen
import com.example.citiway.ui.screens.JourneySelectionScreen
import com.example.citiway.ui.screens.JourneySummaryScreen
import com.example.citiway.ui.screens.ProgressTrackerScreen
import com.example.citiway.ui.screens.SchedulesScreen
import com.example.citiway.ui.screens.SplashScreen
import com.example.citiway.ui.screens.StartLocationScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
        route = ROOT_ROUTE
    ) {
        // Top-level destinations
        composable(route = Screen.Splash.route) { SplashScreen(navController = navController) }
        composable(route = Screen.ProgressTracker.route) { ProgressTrackerScreen(navController = navController) }
        composable(route = Screen.Help.route) { HelpScreen(navController = navController) }

        // Home Graph
        navigation(startDestination = Screen.Home.route, route = HOME_ROUTE) {
            composable(route = Screen.Home.route) { HomeScreen(navController = navController) }
            composable(route = Screen.Schedules.route) { SchedulesScreen(navController = navController) }
            composable(route = Screen.Favourites.route) { FavouritesScreen(navController = navController) }
        }

        // Journey Selection Graph
        navigation(
            startDestination = Screen.DestinationSelection.route,
            route = JOURNEY_SELECTION_ROUTE
        ) {
            composable(route = Screen.DestinationSelection.route) {
                DestinationSelectionScreen(
                    navController = navController
                )
            }
            composable(route = Screen.StartLocationSelection.route) {
                StartLocationScreen(
                    navController = navController
                )
            }
            composable(route = Screen.JourneySelection.route) { JourneySelectionScreen(navController = navController) }
        }

        // Trips Graph
        navigation(startDestination = Screen.JourneyHistory.route, route = TRIPS_ROUTE) {
            composable(route = Screen.JourneyHistory.route) { JourneyHistoryScreen(navController = navController) }
            composable(route = Screen.JourneySummary.route) { JourneySummaryScreen(navController = navController) }
        }
    }
}