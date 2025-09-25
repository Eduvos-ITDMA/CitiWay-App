package com.example.citiway.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    object Home : BottomNavScreen("Home", HOME_ROUTE, Icons.Default.Home)
    object Plan : BottomNavScreen("Plan", JOURNEY_SELECTION_ROUTE, Icons.Default.LocationOn)
    object Trips : BottomNavScreen("Trips", TRIPS_ROUTE, Icons.Default.Favorite)
    object Settings : BottomNavScreen(
        "Settings", "", Icons.Default.Settings
    ) // Navigation action for Settings item is overridden
}