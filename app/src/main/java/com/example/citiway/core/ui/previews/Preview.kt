package com.example.citiway.core.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.navigation.compose.rememberNavController
import com.example.citiway.CitiWayApp
import com.example.citiway.core.navigation.routes.HOME_ROUTE
import com.example.citiway.core.ui.theme.CitiWayTheme

@Preview(showBackground = true)
@Composable
fun PreviewApp(@PreviewParameter(ScreenRouteProvider::class) startRoute: String = HOME_ROUTE) {
    val navController = rememberNavController()

    CitiWayTheme {
        CitiWayApp(navController, startRoute)
    }
}
