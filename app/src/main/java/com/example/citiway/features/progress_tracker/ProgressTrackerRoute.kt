package com.example.citiway.features.progress_tracker

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper

@Composable
fun ProgressTrackerRoute(
    navController: NavController,
    drawerState: DrawerState,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, drawerState, true) {paddingValues ->
        // ProgressTrackerContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    }
}
