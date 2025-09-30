package com.example.citiway.features.start_location_selection

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper

@Composable
fun StartLocationSelectionRoute(
    navController: NavController,
    drawerState: DrawerState,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, drawerState, true) {paddingValues ->
        // StartLocationSelectionContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    }
}
