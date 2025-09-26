package com.example.citiway.ui.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.citiway.ui.navigation.routes.HOME_ROUTE
import com.example.citiway.ui.navigation.routes.ROOT_ROUTE

@Composable
fun SetupNavGraph(navController: NavHostController, drawerState: DrawerState) {
    NavHost(
        navController = navController, startDestination = HOME_ROUTE, route = ROOT_ROUTE
    ) {
        // Top-level destinations
        topLevelDestinations(navController, drawerState)
        homeNavGraph(navController, drawerState)
        journeySelectionGraph(navController, drawerState)
        tripsNavGraph(navController, drawerState)
    }
}