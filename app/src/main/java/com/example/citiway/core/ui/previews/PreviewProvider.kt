package com.example.citiway.core.ui.previews

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.citiway.core.navigation.routes.Screen

class ScreenRouteProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        Screen.Home.route,
        Screen.DestinationSelection.route,
        Screen.StartLocationSelection.route,
        Screen.Schedules.route,
        Screen.Favourites.route,
        Screen.JourneySelection.route,
        Screen.ProgressTracker.route,
        Screen.JourneySummary.route,
        Screen.Help.route,
        Screen.JourneyHistory.route,
        Screen.Splash.route,
    )
}