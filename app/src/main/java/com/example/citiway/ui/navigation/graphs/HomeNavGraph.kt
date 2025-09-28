package com.example.citiway.ui.navigation.graphs

import androidx.compose.material3.DrawerState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.citiway.ui.navigation.components.ScreenWrapper
import com.example.citiway.ui.navigation.routes.HOME_ROUTE
import com.example.citiway.ui.navigation.routes.Screen
import com.example.citiway.ui.screens.FavouritesScreen
import com.example.citiway.ui.screens.HomeScreen
import com.example.citiway.ui.screens.SchedulesScreen

fun NavGraphBuilder.homeNavGraph(
    navController: NavController, drawerState: DrawerState
) {
    navigation(startDestination = Screen.Home.route, route = HOME_ROUTE) {
        // Home screen
        composable(Screen.Home.route) {
            ScreenWrapper(navController, drawerState, true) { nav, paddingValues ->
                HomeScreen(nav, paddingValues)
            }
        }

        // Schedules screen
        composable(Screen.Schedules.route) {
            ScreenWrapper(navController, drawerState, true) { nav, paddingValue ->
                SchedulesScreen(nav, paddingValue)
            }
        }

        // Favourites screen
        composable(Screen.Favourites.route) {
            ScreenWrapper(navController, drawerState, true) { nav, paddingValue ->
                FavouritesScreen(nav, paddingValue)
            }
        }
    }
}
