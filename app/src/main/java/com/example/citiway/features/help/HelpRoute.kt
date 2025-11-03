package com.example.citiway.features.help

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.HelpViewModel

@Composable
fun HelpRoute(
    navController: NavController,
) {
    val helpViewModel: HelpViewModel = viewModel(
        factory = viewModelFactory {
            HelpViewModel()
        }
    )

    val userInfoState by helpViewModel.screenState.collectAsStateWithLifecycle()

    ScreenWrapper(navController, true, { paddingValues ->
        HelpContent(
            paddingValues = paddingValues,
            userEmail = userInfoState.userEmail,
            userName = userInfoState.userName
        )
    })
}