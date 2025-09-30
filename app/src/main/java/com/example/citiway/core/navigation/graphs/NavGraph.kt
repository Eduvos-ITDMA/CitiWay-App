package com.example.citiway.core.navigation.graphs

/** This file defines the main navigation graph for the application using Jetpack Navigation.
 * The nav graph contains all of the app's destinations and their organization hierarchy.
 *
 * The SetupNavGraph composable here acts as the central hub for defining how different
 * screens (or groups of screens, AKA nested navigation graphs) are connected.
 * It sets up the NavHost, which is the container for swapping different composable
 * destinations.
 */

import androidx.compose.material3.DrawerState
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
    drawerState: DrawerState,
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
        navController = navController, startDestination = startGraph, route = ROOT_ROUTE
    ) {
        topLevelDestinations(navController, drawerState)
        homeNavGraph(navController, drawerState)
        journeySelectionGraph(navController, drawerState)
        tripsNavGraph(navController, drawerState)
    }


    // Conditionally navigation to a specific screen when a SCREEN route was passed
    // and it is not the first screen of the specified graph
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