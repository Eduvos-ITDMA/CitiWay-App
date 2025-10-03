package com.example.citiway.features.schedules

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper

@Composable
fun SchedulesRoute(
    navController: NavController,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, true, { paddingValues ->
        // SchedulesContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    })
}