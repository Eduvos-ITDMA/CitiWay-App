package com.example.citiway.features.stats

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.di.viewModelFactory

@Composable
fun StatsRoute(
    navController: NavController,
    viewModel: StatsViewModel = viewModel(
        factory = viewModelFactory {
            StatsViewModel()
        }
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScreenWrapper(navController, true) { paddingValues ->
        StatsContent(
            paddingValues = paddingValues,
            state = state,
            onRefresh = viewModel::refresh
        )
    }
}