package com.example.citiway.features.help

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper

@Composable
fun HelpRoute(
    navController: NavController,
    drawerState: DrawerState,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, drawerState, true) {paddingValues ->
        // HelpContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    }
}
