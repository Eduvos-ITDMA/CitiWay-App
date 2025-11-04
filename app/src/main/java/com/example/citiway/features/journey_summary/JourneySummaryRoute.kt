package com.example.citiway.features.journey_summary

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.citiway.core.utils.ScreenWrapper

@Composable
fun JourneySummaryRoute(
    navController: NavController,
) {

    ScreenWrapper(navController, true, { paddingValues ->
         JourneySummaryContent(
            paddingValues = paddingValues,
             navController = navController,
        )
    })
}
