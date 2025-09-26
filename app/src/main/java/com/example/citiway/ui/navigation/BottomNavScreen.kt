package com.example.citiway.ui.navigation

import androidx.annotation.DrawableRes
import com.example.citiway.R

sealed class BottomNavScreen(
    val title: String,
    val route: String,
    @DrawableRes val iconResId: Int
) {
    object Home : BottomNavScreen("Home", HOME_ROUTE, R.drawable.ic_home)
    object Plan : BottomNavScreen("Plan", JOURNEY_SELECTION_ROUTE, R.drawable.ic_map)
    object Trips : BottomNavScreen("Trips", TRIPS_ROUTE, R.drawable.ic_travel)
    object Settings : BottomNavScreen(
        "Settings", "", R.drawable.ic_settings
    ) // Navigation action for Settings item is overridden
}