package com.example.citiway.features.journey_history

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper

@Composable
fun JourneyHistoryRoute(
    navController: NavController,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, true, { paddingValues ->
        // JourneyHistoryContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    })
}
