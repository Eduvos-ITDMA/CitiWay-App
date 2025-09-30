package com.example.citiway.features.home

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper
import com.example.citiway.features.shared.CompletedJourneysViewModel

@Composable
fun HomeRoute(
    navController: NavController,
    drawerState: DrawerState,
    viewModel: CompletedJourneysViewModel = viewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    ScreenWrapper(navController, drawerState, true) {paddingValues ->
        HomeContent(
            state = state,
            paddingValues = paddingValues,
            navController,
            viewModel::toggleFavourite
        )
    }
}
