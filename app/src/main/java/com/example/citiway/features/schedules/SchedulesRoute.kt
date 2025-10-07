package com.example.citiway.features.schedules

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper

@Composable
fun SchedulesRoute(
    navController: NavController,
) {
    ScreenWrapper(navController, true, { paddingValues ->
        SchedulesContent(
            paddingValues = paddingValues,
        )
    })
}