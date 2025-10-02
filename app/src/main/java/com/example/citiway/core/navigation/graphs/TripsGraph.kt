package com.example.citiway.core.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.navigation.routes.TRIPS_ROUTE
import com.example.citiway.features.journey_history.JourneyHistoryRoute
import com.example.citiway.features.journey_summary.JourneySummaryRoute

fun NavGraphBuilder.tripsNavGraph(
    navController: NavController, drawerState: DrawerState
) {
    navigation(startDestination = Screen.JourneyHistory.route, route = TRIPS_ROUTE) {
        // Journey History screen
        composable(Screen.JourneyHistory.route) {
            JourneyHistoryRoute(navController, drawerState)
        }

        // Journey Summary screen
        composable(Screen.JourneySummary.route) {
            JourneySummaryRoute(navController, drawerState)
        }
    }
}
