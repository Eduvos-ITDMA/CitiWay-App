package com.example.citiway.features.favourites

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper

@Composable
fun FavouritesRoute(
    navController: NavController,
    drawerState: DrawerState,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, drawerState, true) {paddingValues ->
        // FavouritesContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    }
}
