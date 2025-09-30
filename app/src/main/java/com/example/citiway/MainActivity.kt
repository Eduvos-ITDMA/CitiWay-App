package com.example.citiway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.citiway.core.ui.components.Drawer
import com.example.citiway.core.navigation.graphs.SetupNavGraph
import com.example.citiway.core.navigation.routes.HOME_ROUTE
import com.example.citiway.ui.theme.CitiWayTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyD71E9VvLNwHYV5ESHN_vrODGkzX2MPOs4")
        }

        enableEdgeToEdge()
        setContent {
            CitiWayTheme {
                // Set up navigation
                val navController = rememberNavController()

                CitiWayApp(navController)
            }
        }
    }
}

@Composable
fun CitiWayApp(navController: NavHostController, startRoute: String = HOME_ROUTE) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

//    NavStackLogger(navController)

    Drawer(drawerState) {
        SetupNavGraph(navController, drawerState, startRoute)
    }
}
