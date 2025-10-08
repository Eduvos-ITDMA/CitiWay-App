package com.example.citiway.core.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.citiway.core.navigation.routes.JOURNEY_SELECTION_ROUTE
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.features.destination_selection.DestinationSelectionRoute
import com.example.citiway.features.journey_selection.JourneySelectionRoute
import com.example.citiway.features.start_location_selection.StartLocationSelectionRoute

fun NavGraphBuilder.journeySelectionGraph(
    navController: NavController,
) {
    navigation(
        startDestination = Screen.DestinationSelection.route, route = JOURNEY_SELECTION_ROUTE
    ) {
        // Destination Selection screen
        composable(Screen.DestinationSelection.route) {
            DestinationSelectionRoute(navController)
        }

        // Start Location Selection screen
        composable(Screen.StartLocationSelection.route) {
            StartLocationSelectionRoute(navController)
        }

        // Journey Selection screen
        composable(Screen.JourneySelection.route) {
            JourneySelectionRoute(navController)
        }
    }
}
