package com.example.citiway.core.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.navigation.routes.TRIPS_ROUTE
import com.example.citiway.features.journey_history.JourneyHistoryRoute
import com.example.citiway.features.journey_summary.JourneySummaryRoute

fun NavGraphBuilder.tripsNavGraph(
    navController: NavController
) {
    navigation(startDestination = Screen.JourneyHistory.route, route = TRIPS_ROUTE) {
        // Journey History screen
        composable(Screen.JourneyHistory.route) {
            JourneyHistoryRoute(navController)
        }

        // Journey Summary screen
        composable(
            route = Screen.JourneySummary.route,
            arguments = listOf(
                navArgument("journeyId") { type = NavType.IntType },
                navArgument("primaryButtonAction") {
                    type = NavType.StringType
                    defaultValue = "repeat"
                }
            )
        ) { backStackEntry ->
            val journeyId = backStackEntry.arguments?.getInt("journeyId")
            val primaryButtonAction = backStackEntry.arguments?.getString("primaryButtonAction")
            JourneySummaryRoute(navController, journeyId, primaryButtonAction)}
    }
}
