package com.example.citiway.ui.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.citiway.ui.navigation.components.ScreenWrapper
import com.example.citiway.ui.navigation.routes.Screen
import com.example.citiway.ui.navigation.routes.TRIPS_ROUTE
import com.example.citiway.ui.screens.JourneyHistoryScreen
import com.example.citiway.ui.screens.JourneySummaryScreen

fun NavGraphBuilder.tripsNavGraph(
    navController: NavController, drawerState: DrawerState
) {
    navigation(startDestination = Screen.JourneyHistory.route, route = TRIPS_ROUTE) {
        // Journey History screen
        composable(Screen.JourneyHistory.route) {
            ScreenWrapper(navController, drawerState, true) { nav, paddingValues ->
                JourneyHistoryScreen(nav, paddingValues)
            }
        }

        // Journey Summary screen
        composable(Screen.JourneySummary.route) {
            ScreenWrapper(navController, drawerState, true) { nav, paddingValues ->
                JourneySummaryScreen(nav, paddingValues)
            }
        }
    }
}
