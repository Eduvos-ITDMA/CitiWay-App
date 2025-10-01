package com.example.citiway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.citiway.ui.navigation.components.Drawer
import com.example.citiway.ui.navigation.graphs.SetupNavGraph
import com.example.citiway.ui.navigation.routes.HOME_ROUTE
import com.example.citiway.ui.theme.CitiWayTheme
import com.example.citiway.viewmodel.DrawerViewModel
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

            // Initializing DrawerViewModel to manage app settings (theme, location, etc.)
            val drawerViewModel: DrawerViewModel = viewModel()

            // Collect dark mode state from DataStore
            // This makes theme independent of system settings and persists across app restarts
            val darkModeEnabled by drawerViewModel.darkModeEnabled.collectAsState()

            // Apply theme based on user's preference from drawer toggle, not *system settings*
            CitiWayTheme {
                // Setting up navigation
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

    // Passing navController to Drawer so it can handle navigation to different screens
    Drawer(
        drawerState = drawerState,
        navController = navController
    ) {
        SetupNavGraph(navController, drawerState, startRoute)
    }
}
