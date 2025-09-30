package com.example.citiway.core.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.citiway.core.navigation.routes.HOME_ROUTE
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.features.favourites.FavouritesRoute
import com.example.citiway.features.home.HomeRoute
import com.example.citiway.features.schedules.SchedulesRoute

fun NavGraphBuilder.homeNavGraph(
    navController: NavController, drawerState: DrawerState
) {
    navigation(startDestination = Screen.Home.route, route = HOME_ROUTE) {
        // Home screen
        composable(Screen.Home.route) {
            HomeRoute(navController, drawerState)
        }

        // Schedules screen
        composable(Screen.Schedules.route) {
            SchedulesRoute(navController, drawerState)
        }

        // Favourites screen
        composable(Screen.Favourites.route) {
            FavouritesRoute(navController, drawerState)
        }
    }
}
