package com.example.citiway.features.journey_selection

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper

@Composable
fun JourneySelectionRoute(
    navController: NavController,
    drawerState: DrawerState,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, drawerState, true) {paddingValues ->
        // JourneySelectionContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    }
}
