package com.example.citiway.features.progress_tracker

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.features.journey_progress.ProgressTrackerContent
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.JourneyViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProgressTrackerRoute(
    navController: NavController,
) {
    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        }
    )

    ScreenWrapper(navController, true) { paddingValues ->
        ProgressTrackerContent(
            navController = navController,
            paddingValues = paddingValues
        )
    }
}