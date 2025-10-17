package com.example.citiway.core.navigation.graphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.citiway.core.navigation.routes.GraphRoutes
import com.example.citiway.core.navigation.routes.HOME_ROUTE
import com.example.citiway.core.navigation.routes.ROOT_ROUTE
import com.example.citiway.core.navigation.routes.ScreenToGraphMap

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startRoute: String = HOME_ROUTE
) {


    // Determine start graph
    val startGraph = if (startRoute in GraphRoutes) {
        startRoute
    } else {
        ScreenToGraphMap[startRoute] ?: HOME_ROUTE
    }
    val targetScreen = if (startRoute in GraphRoutes) null else startRoute

    // Set up NavHost
    NavHost(
        navController = navController,
        startDestination = startGraph,
        route = ROOT_ROUTE
    ) {
        topLevelDestinations(navController)
        homeNavGraph(navController) // Pass it here
        journeySelectionGraph(navController)
        tripsNavGraph(navController) // And here if needed
    }

    // Conditionally navigate to a specific screen when a SCREEN route was passed
    if (targetScreen != null) {
        LaunchedEffect(key1 = targetScreen) {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute == startGraph) {
                navController.navigate(targetScreen) {
                    popUpTo(startGraph) { inclusive = true }
                }
            }
        }
    }
}