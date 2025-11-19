package com.example.citiway.core.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.citiway.core.ui.components.ConfirmationDialog
import com.example.citiway.core.ui.theme.CitiWayTheme
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.features.destination_selection.DestinationSelectionContent
import com.example.citiway.features.favourites.FavouritesContent
import com.example.citiway.features.help.HelpContent
import com.example.citiway.features.home.HomeActions
import com.example.citiway.features.home.HomeContent
import com.example.citiway.features.journey_history.JourneyHistoryContent
import com.example.citiway.features.journey_selection.JourneySelectionContent
import com.example.citiway.features.journey_selection.JourneySelectionScreenActions
import com.example.citiway.features.journey_selection.LocationFieldActions
import com.example.citiway.features.journey_summary.JourneySummaryContent
import com.example.citiway.features.progress_tracker.ProgressTrackerContent
import com.example.citiway.features.schedules.SchedulesContent
import com.example.citiway.features.shared.CompletedJourneysState
import com.example.citiway.features.shared.Instruction
import com.example.citiway.features.shared.Journey
import com.example.citiway.features.shared.JourneySelectionActions
import com.example.citiway.features.shared.JourneyState
import com.example.citiway.features.shared.MapActions
import com.example.citiway.features.shared.MapState
import com.example.citiway.features.shared.Stop
import com.example.citiway.features.shared.StopType
import com.example.citiway.features.start_location_selection.StartLocationSelectionContent
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.Duration
import java.time.Instant

private val mockMapActions = MapActions(
    selectLocationOnMap = {},
    updateLocationPermission = {}
)

private val mockJourney = Journey(
    stops = listOf(
        Stop(
            name = "Mowbray Station",
            stopType = StopType.DEPARTURE,
            latLng = LatLng(-33.9455, 18.4756),
            nextEventIn = Duration.ofMinutes(5),
            nextEventInMin = 5,
            routeName = "Southern Line",
            travelMode = "HEAVY_RAIL",
        ),
        Stop(
            name = "Salt River Station",
            stopType = StopType.ARRIVAL,
            latLng = LatLng(-33.9295, 18.4528),
            nextEventIn = Duration.ofMinutes(5),
            nextEventInMin = 5,
            travelMode = "HEAVY_RAIL",
        ),
        Stop(
            name = "Salt River Rail North",
            stopType = StopType.DEPARTURE,
            latLng = LatLng(-33.9275, 18.4533),
            nextEventIn = Duration.ofMinutes(5),
            nextEventInMin = 5,
            routeName = "260 Omuramba",
            travelMode = "BUS",
        ),
        Stop(
            name = "Quest",
            stopType = StopType.ARRIVAL,
            latLng = LatLng(-33.8953, 18.5147),
            nextEventIn = Duration.ofMinutes(5),
            nextEventInMin = 5,
            travelMode = "BUS",
        ),
        Stop(
            name = "Oasis",
            stopType = StopType.DEPARTURE,
            latLng = LatLng(-33.8935, 18.5085),
            nextEventIn = Duration.ofMinutes(5),
            nextEventInMin = 5,
            routeName = "262 SummerGreens",
            travelMode = "BUS",
        ),
        Stop(
            name = "Final stop",
            stopType = StopType.ARRIVAL,
            latLng = LatLng(-33.8935, 18.5085),
            nextEventIn = Duration.ofMinutes(5),
            nextEventInMin = 5,
        )
    ),
    instructions = listOf(
        Instruction(
            text = "Walk 350m",
            durationMinutes = 6,
            travelMode = "WALK"
        ),
        Instruction(
            text = "Take train for 2 stations",
            durationMinutes = 5,
            travelMode = "HEAVY_RAIL"
        ),
        Instruction(
            text = "Walk 329m",
            durationMinutes = 8,
            travelMode = "WALK"
        ),
        Instruction(
            text = "Take MyCiTi bus for 13 stops",
            durationMinutes = 22,
            travelMode = "BUS"
        ),
        Instruction(
            text = "Walk 42m",
            durationMinutes = 1,
            travelMode = "WALK"
        ),
        Instruction(
            text = "Take MyCiTi bus for 5 stops",
            durationMinutes = 13,
            travelMode = "BUS"
        ),
        Instruction(
            text = "Walk 89m",
            durationMinutes = 1,
            travelMode = "WALK"
        )
    ),
    startTime = Instant.parse("2025-10-21T12:41:00Z"),
    arrivalTime = Instant.parse("2025-10-21T16:06:00Z"),
    fareTotal = 13.50,
)

val mockPlacesActions = PlacesActions(
    onSetSearchText = {},
    onSearchPlaces = {},
    onSelectPlace = {},
    getPlace = { prediction ->
        val mockLatLng = LatLng(34.0522, -118.2437) // Example: Los Angeles coordinates
        SelectedLocation(
            latLng = mockLatLng,
            placeId = "mock_id_${prediction.getPrimaryText(null)}",
            primaryText = "Mock Address"
        )
    },
    onClearSearch = {},
    onSetSelectedLocation = {},
    onUseUserLocation = {},
    getPlaceFromLatLng = { latLng ->
        SelectedLocation(
            latLng = latLng,
            placeId = "mock_id",
            primaryText = "Mock Address",

        )
    },
    onClearLocations = {}

)

private val mockJourneySelectionActions = JourneySelectionScreenActions(
    JourneySelectionActions(
        { timeType -> },
        { string -> },
        { location -> },
        { location -> },
        {},
        { l, m -> },
        {},
        {}
    ),
    {},
    LocationFieldActions({}, { autocompletePrediction -> }),
    LocationFieldActions({}, { autocompletePrediction -> })
)


// Home Screen Preview
@Preview(showBackground = true, name = "Home Screen")
@Composable
fun HomeScreenPreview() {
    val mockHomeActions = HomeActions(
        onToggleFavourite = {},
        onSchedulesLinkClick = {},
        onMapIconClick = {},
        onSelectPrediction = { prediction -> },
        onFavouritesTitleClick = {},
        onRecentTitleClick = {},
        onViewJourneySummary = {},
        onStartJourney = TODO(),
    )

    CitiWayTheme {
        HomeContent(
            completedJourneysState = CompletedJourneysState(),
            homeActions = mockHomeActions,
            placesState = PlacesState(),
            placesActions = mockPlacesActions,
            paddingValues = PaddingValues(),
        )
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
                placesState = PlacesState(),
                placesActions = mockPlacesActions,
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
        )
    }
}

// Journey Selection Screen Preview
@Preview(showBackground = true, name = "Journey Selection Screen")
@Composable
fun JourneySelectionScreenPreview() {
    CitiWayTheme {
        JourneySelectionContent(
            state = JourneyState(),
            actions = mockJourneySelectionActions,
            startPlacesState = PlacesState(),
            startPlacesActions = mockPlacesActions,
            destPlacesState = PlacesState(),
            destPlacesActions = mockPlacesActions,
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
            journey = mockJourney,
            startLocation = SelectedLocation(LatLng(0.0, 0.0), "mock_id", "Mowbray"),
            destination = SelectedLocation(LatLng(0.0, 0.0), "mock_id_2", "Century City"),
            paddingValues = PaddingValues(),
            navController = rememberNavController(),
            onDone = {}
        )
    }
}

// Progress Tracker Screen Preview
@Preview(showBackground = true, name = "Progress Tracker Screen")
@Composable
fun ProgressTrackerScreenPreview() {
    val mockJourneyState = JourneyState(
        SelectedLocation(LatLng(0.0, 0.0), "mock_id", "Mowbray"),
        SelectedLocation(LatLng(0.0, 0.0), "mock_id_2", "Century City"),
        null,
        mockJourney,
        null,
    )

    CitiWayTheme {
        ProgressTrackerContent(
            journeyState = mockJourneyState,
            paddingValues = PaddingValues(),
            navController = rememberNavController(), // Or callbacks like onCancelJourney
            {},
            onJourneyComplete = {}
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
            placesState = PlacesState(),
            placesActions = mockPlacesActions,
            onPermissionRequest = {},
            cameraPositionState = rememberCameraPositionState(),
            onConfirmLocation = {},
            locationEnabledInApp = true,
            isLocationPermissionGranted = true,
            onEnableLocation = {},
            onRequestSystemPermission = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmationDialogPreview() {
    ConfirmationDialog(
        visible = true,
        title = "Are you sure you want to cancel your trip?",
        message = "You will be redirected to the Home Page",
        confirmText = "Continue",
        dismissText = "No, go back",
        onConfirm = {},
        onDismiss = {},
//        onClose = {}
    )
}