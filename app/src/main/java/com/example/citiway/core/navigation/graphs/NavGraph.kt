package com.example.citiway.core.navigation.graphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.citiway.core.navigation.routes.GraphRoutes
import com.example.citiway.core.navigation.routes.HOME_ROUTE
import com.example.citiway.core.navigation.routes.ROOT_ROUTE
import com.example.citiway.core.navigation.routes.Screen
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

    // Determine the actual start destination for NavHost
    val actualStartDestination = when {
        // If we want to start at a screen in ROOT_ROUTE, use that screen directly
        startGraph == ROOT_ROUTE && targetScreen != null -> targetScreen
        // If startGraph is ROOT_ROUTE but no specific screen, default to splash
        startGraph == ROOT_ROUTE -> Screen.Splash.route
        // Otherwise use the determined graph
        else -> startGraph
    }

    // Set up NavHost
    NavHost(
        navController = navController,
        startDestination = actualStartDestination,
        route = ROOT_ROUTE
    ) {
        topLevelDestinations(navController)
        homeNavGraph(navController)
        journeySelectionGraph(navController)
        tripsNavGraph(navController)
    }

    // Conditionally navigate to a specific screen when a SCREEN route was passed
    // Only do this if we're NOT already starting at that screen
    if (targetScreen != null && actualStartDestination != targetScreen) {
        LaunchedEffect(key1 = targetScreen) {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute == startGraph || currentRoute == actualStartDestination) {
                navController.navigate(targetScreen) {
                    popUpTo(actualStartDestination) { inclusive = true }
                }
            }
        }
    }

}