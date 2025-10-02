package com.example.citiway.features.schedules

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper
import com.example.citiway.features.shared.CompletedJourneysViewModel

@Composable
fun SchedulesRoute(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: CompletedJourneysViewModel = viewModel()
) {
    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, drawerState, true) {paddingValues ->
         SchedulesContent(
            state = state,
            paddingValues = paddingValues
        )
    }
}