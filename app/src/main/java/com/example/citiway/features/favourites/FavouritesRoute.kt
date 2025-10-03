package com.example.citiway.features.favourites

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper

@Composable
fun FavouritesRoute(
    navController: NavController,
) {
//    val state by viewModel.screenState.collectAsState()

    ScreenWrapper(navController, true, { paddingValues ->
        // FavouritesContent(
        //    state = state,
        //    paddingValues = paddingValues
        //)
    })
}
