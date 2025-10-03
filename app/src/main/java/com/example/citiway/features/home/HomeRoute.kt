package com.example.citiway.features.home

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
    viewModel: CompletedJourneysViewModel = viewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    ScreenWrapper(navController, true, { paddingValues ->
        HomeContent(
            state = state,
            paddingValues = paddingValues,
            navController,
            viewModel::toggleFavourite
        )
    })
}
