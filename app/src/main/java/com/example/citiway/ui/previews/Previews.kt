package com.example.citiway.ui.previews

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.citiway.ui.navigation.components.ScreenWrapper
import com.example.citiway.ui.screens.DestinationSelectionScreen
import com.example.citiway.ui.screens.FavouritesScreen
import com.example.citiway.ui.screens.HelpScreen
import com.example.citiway.ui.screens.HomeScreen
import com.example.citiway.ui.screens.JourneyHistoryScreen
import com.example.citiway.ui.screens.JourneySelectionScreen
import com.example.citiway.ui.screens.JourneySummaryScreen
import com.example.citiway.ui.screens.ProgressTrackerScreen
import com.example.citiway.ui.screens.SchedulesScreen
import com.example.citiway.ui.screens.SplashScreen
import com.example.citiway.ui.screens.StartLocationScreen
import com.example.citiway.ui.theme.CitiWayTheme

// Home Screen Preview
@Preview(showBackground = true, name = "Home Screen")
@Composable
fun HomeScreenPreview() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    CitiWayTheme {
        ScreenWrapper(rememberNavController(), drawerState, true) { nav, paddingValues ->
            HomeScreen(nav, paddingValues)
        }
    }
}

// Splash Screen Preview
@Preview(showBackground = true, name = "Splash Screen")
@Composable
fun SplashScreenPreview() {
    CitiWayTheme {
        SplashScreen(navController = rememberNavController())
    }
}

// Favourites Screen Preview
@Preview(showBackground = true, name = "Favourites Screen")
@Composable
fun FavouritesScreenPreview() {
    CitiWayTheme {
        ScreenWrapper(
            rememberNavController(),
            rememberDrawerState(DrawerValue.Closed),
            true
        ) { nav, paddingValues ->
            FavouritesScreen(nav, paddingValues)
        }
    }
}

// Destination Selection Screen Preview
@Preview(showBackground = true, name = "Destination Selection Screen")
@Composable
fun DestinationSelectionScreenPreview() {
    CitiWayTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ScreenWrapper(rememberNavController(), drawerState, true) { nav, paddingValues ->
            DestinationSelectionScreen(nav, paddingValues)
        }
    }
}

// Help Screen Preview
@Preview(showBackground = true, name = "Help Screen")
@Composable
fun HelpScreenPreview() {
    CitiWayTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ScreenWrapper(rememberNavController(), drawerState, true) { nav, paddingValues ->
            HelpScreen(nav, paddingValues)
        }
    }
}

// Journey Selection Screen Preview
@Preview(showBackground = true, name = "Journey Selection Screen")
@Composable
fun JourneySelectionScreenPreview() {
    CitiWayTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ScreenWrapper(rememberNavController(), drawerState, true) { nav, paddingValues ->
            JourneySelectionScreen(nav, paddingValues)
        }
    }
}

// Journey Summary Screen Preview
@Preview(showBackground = true, name = "Journey Summary Screen")
@Composable
fun JourneySummaryScreenPreview() {
    CitiWayTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ScreenWrapper(rememberNavController(), drawerState, true) { nav, paddingValues ->
            JourneySummaryScreen(nav, paddingValues)
        }
    }
}

// Progress Tracker Screen Preview
@Preview(showBackground = true, name = "Progress Tracker Screen")
@Composable
fun ProgressTrackerScreenPreview() {
    CitiWayTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ScreenWrapper(rememberNavController(), drawerState, true) { nav, paddingValues ->
            ProgressTrackerScreen(nav, paddingValues)
        }
    }
}

// Route History Screen Preview
@Preview(showBackground = true, name = "Route History Screen")
@Composable
fun JourneyHistoryScreenPreview() {
    CitiWayTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ScreenWrapper(rememberNavController(), drawerState, true) { nav, paddingValues ->
            JourneyHistoryScreen(nav, paddingValues)
        }
    }
}

// Schedules Screen Preview
@Preview(showBackground = true, name = "Schedules Screen")
@Composable
fun SchedulesScreenPreview() {
    CitiWayTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ScreenWrapper(rememberNavController(), drawerState, true) { nav, paddingValues ->
            SchedulesScreen(nav, paddingValues)
        }
    }
}

// Start Location Screen Preview
@Preview(showBackground = true, name = "Start Location Screen")
@Composable
fun StartLocationScreenPreview() {
    CitiWayTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ScreenWrapper(rememberNavController(), drawerState, true) { nav, paddingValues ->
            StartLocationSelectionScreen(nav, paddingValues)
        }
    }
}