package com.example.citiway.features.schedules

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable


import androidx.navigation.NavController
import com.example.citiway.core.util.ScreenWrapper


@Composable
fun SchedulesRoute(
    navController: NavController,
    drawerState: DrawerState,
) {
    ScreenWrapper(navController, drawerState, true) {paddingValues ->
         SchedulesContent(
            paddingValues = paddingValues,
        )
    }
}