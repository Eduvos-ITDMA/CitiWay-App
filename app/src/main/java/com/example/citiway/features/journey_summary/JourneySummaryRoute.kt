package com.example.citiway.features.journey_summary

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.JourneyViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun JourneySummaryRoute(
    navController: NavController,
) {
    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        }
    )
    val journey = journeyViewModel.state.collectAsState().value.journey

    ScreenWrapper(navController, true, { paddingValues ->
        JourneySummaryContent(
            journey = journey,
            paddingValues = paddingValues,
            navController = navController,
        )
    })
}
