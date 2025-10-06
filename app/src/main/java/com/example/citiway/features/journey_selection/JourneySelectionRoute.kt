package com.example.citiway.features.journey_selection

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper

@Composable
fun JourneySelectionRoute(
    navController: NavController,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, true, { paddingValues ->
        // JourneySelectionContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    })
}
