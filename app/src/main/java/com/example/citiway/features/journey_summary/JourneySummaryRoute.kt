package com.example.citiway.features.journey_summary

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper

@Composable
fun JourneySummaryRoute(
    navController: NavController,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, true, { paddingValues ->
        // JourneySummaryContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    })
}
