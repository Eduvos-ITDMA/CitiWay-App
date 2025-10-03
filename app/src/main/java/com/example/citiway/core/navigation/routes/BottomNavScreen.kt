package com.example.citiway.core.navigation.routes

import androidx.annotation.DrawableRes
import com.example.citiway.R

sealed class BottomNavScreen(
    val title: String, val route: String, @DrawableRes val iconResId: Int
) {
    object Home : BottomNavScreen("Home", HOME_ROUTE, R.drawable.ic_home)
    object Plan : BottomNavScreen("Plan", JOURNEY_SELECTION_ROUTE, R.drawable.ic_map)
    object Journey : BottomNavScreen("Journey", Screen.ProgressTracker.route, R.drawable.ic_journey)
    object Trips : BottomNavScreen("Trips", TRIPS_ROUTE, R.drawable.ic_travel)
}