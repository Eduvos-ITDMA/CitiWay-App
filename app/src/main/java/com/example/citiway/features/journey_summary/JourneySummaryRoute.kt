package com.example.citiway.features.journey_summary

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.JourneyViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.features.shared.CompletedJourney
import com.example.citiway.features.shared.CompletedJourneysViewModel

@Composable
fun JourneySummaryRoute(
    navController: NavController,
    journeyId: Int? = null,
    primaryButtonAction: String?
) {
    // Retrieve journey from database
    val repository = App.appModule.repository
    var completedJourney by remember { mutableStateOf<CompletedJourney?>(null) }

    val placesManager = App.appModule.placesManager
    val placesActions = placesManager.actions

    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        })

    val completedJourneysViewModel: CompletedJourneysViewModel = viewModel(
        factory = viewModelFactory {
            CompletedJourneysViewModel(
                placesActions = placesActions,
                journeyViewModel = journeyViewModel,
                navController = navController
            )
        }
    )

    LaunchedEffect(journeyId) {
        if (journeyId != null) {
            completedJourney = journeyViewModel.loadJourneyForSummary(journeyId)
        }
    }

    ScreenWrapper(navController, true, { paddingValues ->
        JourneySummaryContent(
            completedJourney = completedJourney,
            onRepeatJourney = completedJourneysViewModel.actions.onRepeatJourney,
            primaryButtonAction = primaryButtonAction,
            paddingValues = paddingValues,
            navController = navController,
        )
    })
}
