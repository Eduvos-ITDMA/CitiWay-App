package com.example.citiway.ui.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.citiway.ui.navigation.components.ScreenWrapper
import com.example.citiway.ui.navigation.routes.JOURNEY_SELECTION_ROUTE
import com.example.citiway.ui.navigation.routes.Screen
import com.example.citiway.ui.screens.DestinationSelectionScreen
import com.example.citiway.ui.screens.JourneySelectionScreen
import com.example.citiway.ui.screens.StartLocationScreen

fun NavGraphBuilder.journeySelectionGraph(
    navController: NavController, drawerState: DrawerState
) {
    navigation(
        startDestination = Screen.DestinationSelection.route, route = JOURNEY_SELECTION_ROUTE
    ) {
        composable(Screen.DestinationSelection.route) {
            ScreenWrapper(navController, drawerState, true) { nav, paddingValues ->
                DestinationSelectionScreen(
                    nav, paddingValues
                )
            }
        }
        composable(Screen.StartLocationSelection.route) {
            ScreenWrapper(navController, drawerState, true) { nav, paddingValues ->
                StartLocationScreen(
                    nav, paddingValues
                )
            }
        }
        composable(Screen.JourneySelection.route) {
            ScreenWrapper(navController, drawerState, true) { nav, paddingValues ->
                JourneySelectionScreen(nav, paddingValues)
            }
        }
    }
}
