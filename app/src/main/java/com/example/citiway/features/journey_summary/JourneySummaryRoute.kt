package com.example.citiway.features.journey_summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.citiway.App
import com.example.citiway.data.local.CompletedJourney
import com.example.citiway.data.local.entities.CompletedJourneyEntity

@Composable
fun JourneySummaryRoute(
    navController: NavController,
    journeyId: Int? = null
) {
    // Retrieve journey from database
    val repository = App.appModule.repository
    var completedJourney by remember { mutableStateOf<CompletedJourney?>(null) }

    LaunchedEffect(journeyId) {
        if (journeyId != null) {
            val result = repository.getCompletedJourneyById(journeyId)
            completedJourney = result
        }
    }

    ScreenWrapper(navController, true, { paddingValues ->
        JourneySummaryContent(
            completedJourney = completedJourney,
            paddingValues = paddingValues,
            navController = navController,
        )
    })
}
