package com.example.citiway.features.progress_tracker

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.features.journey_progress.ProgressTrackerContent

@Composable
fun ProgressTrackerRoute(
    navController: NavController,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, true) { paddingValues ->
        ProgressTrackerContent(
            navController = navController,
            paddingValues = paddingValues
        )
    }
}