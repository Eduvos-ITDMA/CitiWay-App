package com.example.citiway.features.destination_selection

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper

@Composable
fun DestinationSelectionRoute(
    navController: NavController,
    drawerState: DrawerState,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, drawerState, true) {paddingValues ->
        // DestinationSelectionContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    }
}
