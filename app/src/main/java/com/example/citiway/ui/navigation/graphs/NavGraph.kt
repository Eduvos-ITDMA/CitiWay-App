package com.example.citiway.ui.navigation.graphs

/** This file defines the main navigation graph for the application using Jetpack Navigation.
 * The nav graph contains all of the app's destinations and their organization hierarchy.
 *
 * The SetupNavGraph composable here acts as the central hub for defining how different
 * screens (or groups of screens, AKA nested navigation graphs) are connected.
 * It sets up the NavHost, which is the container for swapping different composable
 * destinations.
 */

import android.util.Log
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.citiway.ui.navigation.routes.HOME_ROUTE
import com.example.citiway.ui.navigation.routes.ROOT_ROUTE

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    drawerState: DrawerState,
    startRoute: String = HOME_ROUTE
) {
    Log.d("Start route", startRoute)
    NavHost(
        navController = navController, startDestination = startRoute, route = ROOT_ROUTE
    ) {
        topLevelDestinations(navController, drawerState)
        homeNavGraph(navController, drawerState)
        journeySelectionGraph(navController, drawerState)
        tripsNavGraph(navController, drawerState)
    }
}