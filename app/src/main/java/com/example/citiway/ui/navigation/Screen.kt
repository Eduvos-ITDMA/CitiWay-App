package com.example.citiway.ui.navigation

const val ROOT_ROUTE = "root"
const val HOME_ROUTE = "home"
const val JOURNEY_SELECTION_ROUTE = "journey_selection"
const val DRAWER_ROUTE = "drawer"

sealed class Screen(val route: String) {
    object Home: Screen(route = "home_screen")
    object DestinationSelection: Screen(route = "destination_selection_screen")
    object StartLocationSelection: Screen(route = "start_location_selection_screen")
    object Schedules: Screen(route = "schedules_screen")
    object Favourites: Screen(route = "favourites_screen")
    object JourneySelection: Screen(route = "journey_selection_screen")
    object ProgressTracker: Screen(route = "progress_tracker_screen")
    object JourneySummary: Screen(route = "journey_summary_screen")
    object Help: Screen(route = "help_screen")
    object RouteHistory: Screen(route = "route_history_screen")
    object Splash: Screen(route = "splash_screen")
}