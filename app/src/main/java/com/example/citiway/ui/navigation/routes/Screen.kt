package com.example.citiway.ui.navigation.routes

const val ROOT_ROUTE = "root"
const val HOME_ROUTE = "home"
const val JOURNEY_SELECTION_ROUTE = "journey_selection"
const val TRIPS_ROUTE = "trips"

sealed class Screen(val route: String) {
    object Home : Screen(route = "home_screen")
    object DestinationSelection : Screen(route = "destination_selection_screen")
    object StartLocationSelection : Screen(route = "start_location_selection_screen")
    object Schedules : Screen(route = "schedules_screen")
    object Favourites : Screen(route = "favourites_screen")
    object JourneySelection : Screen(route = "journey_selection_screen")
    object ProgressTracker : Screen(route = "progress_tracker_screen")
    object JourneySummary : Screen(route = "journey_summary_screen")
    object Help : Screen(route = "help_screen")
    object JourneyHistory : Screen(route = "journey_history_screen")
    object Splash : Screen(route = "splash_screen")
}

// --- Navigation Utility Maps ---

/**
 * A set of all defined navigation graph routes.
 */
val GraphRoutes: Set<String> = setOf(
    ROOT_ROUTE,
    HOME_ROUTE,
    JOURNEY_SELECTION_ROUTE,
    TRIPS_ROUTE
)

/**
 * Maps a Screen Route to its immediate parent NavGraph Route for determining
 * which graph the NavHost should start on when a specific screen is requested.
 */
val ScreenToGraphMap: Map<String, String> = mapOf(

    // HOME_ROUTE graph
    Screen.Home.route to HOME_ROUTE,
    Screen.Schedules.route to HOME_ROUTE,
    Screen.Favourites.route to HOME_ROUTE,
    Screen.Help.route to HOME_ROUTE,
    Screen.JourneyHistory.route to HOME_ROUTE,

    // JOURNEY_SELECTION_ROUTE graph
    Screen.DestinationSelection.route to JOURNEY_SELECTION_ROUTE,
    Screen.StartLocationSelection.route to JOURNEY_SELECTION_ROUTE,
    Screen.JourneySelection.route to JOURNEY_SELECTION_ROUTE,
    Screen.JourneySummary.route to JOURNEY_SELECTION_ROUTE,

    // TRIPS_ROUTE graph
    Screen.ProgressTracker.route to TRIPS_ROUTE,

    // ROOT_ROUTE
    Screen.Splash.route to ROOT_ROUTE
)