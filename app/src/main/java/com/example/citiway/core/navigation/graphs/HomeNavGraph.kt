package com.example.citiway.core.navigation.graphs

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
    navController: NavController,
) {
    navigation(startDestination = Screen.Home.route, route = HOME_ROUTE) {
        // Home screen
        composable(Screen.Home.route) {
            HomeRoute(navController)
        }

        // Schedules screen
        composable(Screen.Schedules.route) {
            SchedulesRoute(navController)
        }

        // Favourites screen
        composable(Screen.Favourites.route) {
            FavouritesRoute(navController)
        }
    }
}
