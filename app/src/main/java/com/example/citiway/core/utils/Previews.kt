package com.example.citiway.core.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.citiway.core.ui.theme.CitiWayTheme
import com.example.citiway.features.SplashScreen
import com.example.citiway.features.destination_selection.DestinationSelectionContent
import com.example.citiway.features.favourites.FavouritesContent
import com.example.citiway.features.help.HelpContent
import com.example.citiway.features.home.HomeActions
import com.example.citiway.features.home.HomeContent
import com.example.citiway.features.journey_history.JourneyHistoryContent
import com.example.citiway.features.journey_selection.JourneySelectionContent
import com.example.citiway.features.journey_summary.JourneySummaryContent
import com.example.citiway.features.progress_tracker.ProgressTrackerContent
import com.example.citiway.features.schedules.SchedulesContent
import com.example.citiway.features.shared.CompletedJourneysState
import com.example.citiway.features.shared.MapActions
import com.example.citiway.features.shared.MapState
import com.example.citiway.features.start_location_selection.StartLocationSelectionContent
import com.google.android.libraries.places.api.model.kotlin.autocompletePrediction
import com.google.maps.android.compose.rememberCameraPositionState

private val mockMapActions = MapActions(
    selectLocationOnMap = {}
)


// Home Screen Preview
@Preview(showBackground = true, name = "Home Screen")
@Composable
fun HomeScreenPreview() {
    val mockHomeActions = HomeActions(
        onToggleFavourite = {},
        onSchedulesLinkClick = {},
        onMapIconClick = {},
        onSelectPrediction = { prediction, placesManager -> },
        onFavouritesTitleClick = {},
        onRecentTitleClick = {}
    )

    CitiWayTheme {
        HomeContent(
            completedJourneysState = CompletedJourneysState(),
            paddingValues = PaddingValues(),
            actions = mockHomeActions
        )
    }
}

// Splash Screen Preview
@Preview(showBackground = true, name = "Splash Screen")
@Composable
fun SplashScreenPreview() {
    CitiWayTheme {
        SplashScreen(navController = rememberNavController()) // Assuming SplashScreen is simple UI
    }
}

// Favourites Screen Preview
@Preview(showBackground = true, name = "Favourites Screen")
@Composable
fun FavouritesScreenPreview() {
    CitiWayTheme {
        FavouritesContent(
            paddingValues = PaddingValues(),
            journeys = emptyList(),
            onToggleFavourite = {}
        )
    }
}

// Destination Selection Screen Preview
@Preview(showBackground = true, name = "Destination Selection Screen")
@Composable
fun DestinationSelectionScreenPreview() {
    CitiWayTheme {
        CitiWayTheme {
            DestinationSelectionContent(
                paddingValues = PaddingValues(),
                state = MapState(),
                actions = mockMapActions,
                cameraPositionState = rememberCameraPositionState(),
                onConfirmLocation = {}
            )
        }
    }
}

// Help Screen Preview
@Preview(showBackground = true, name = "Help Screen")
@Composable
fun HelpScreenPreview() {
    CitiWayTheme {
        HelpContent(
            paddingValues = PaddingValues(),
            navController = rememberNavController(), // Or specific callbacks like onLinkClicked
        )
    }
}

// Journey Selection Screen Preview
@Preview(showBackground = true, name = "Journey Selection Screen")
@Composable
fun JourneySelectionScreenPreview() {
    CitiWayTheme {
        JourneySelectionContent(
            paddingValues = PaddingValues(),
        )
    }
}

// Journey Summary Screen Preview
@Preview(showBackground = true, name = "Journey Summary Screen")
@Composable
fun JourneySummaryScreenPreview() {
    CitiWayTheme {
        JourneySummaryContent(
            paddingValues = PaddingValues(),
            navController = rememberNavController(),
        )
    }
}

// Progress Tracker Screen Preview
@Preview(showBackground = true, name = "Progress Tracker Screen")
@Composable
fun ProgressTrackerScreenPreview() {
    CitiWayTheme {
        ProgressTrackerContent(
            paddingValues = PaddingValues(),
            navController = rememberNavController(), // Or callbacks like onCancelJourney
        )
    }
}

// Journey History Screen Preview
@Preview(
    showBackground = true,
    name = "Journey History Screen"
) // Changed from Route History to match your naming
@Composable
fun JourneyHistoryScreenPreview() {
    CitiWayTheme {
        JourneyHistoryContent(
            paddingValues = PaddingValues(),
            journeys = emptyList(),
        )
    }
}

// Schedules Screen Preview
@Preview(showBackground = true, name = "Schedules Screen")
@Composable
fun SchedulesScreenPreview() {
    CitiWayTheme {
        ->
        SchedulesContent(
            paddingValues = PaddingValues(),
//            navController = rememberNavController(), // Or callbacks like onScheduleSelected
        )
    }
}

// Start Location Screen Preview
@Preview(showBackground = true, name = "Start Location Screen")
@Composable
fun StartLocationScreenPreview() {
    CitiWayTheme {
        StartLocationSelectionContent(
            paddingValues = PaddingValues(),
            state = MapState(),
            actions = mockMapActions,
            onPermissionRequest = {},
            cameraPositionState = rememberCameraPositionState(),
            onConfirmLocation = {},
            locationEnabledInApp = true

        )
    }
}
