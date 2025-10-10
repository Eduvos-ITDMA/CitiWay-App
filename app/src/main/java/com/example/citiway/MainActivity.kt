package com.example.citiway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.citiway.core.navigation.graphs.SetupNavGraph
import com.example.citiway.core.navigation.routes.HOME_ROUTE
import com.example.citiway.core.ui.theme.CitiWayTheme
import com.example.citiway.features.shared.DrawerViewModel
import com.google.android.libraries.places.api.Places
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import com.citiway.data.local.DatabaseTest
//import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.data.local.RecentSearch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        // Test the database
        DatabaseTest(this).runTest()

        enableEdgeToEdge()
        setContent {



            // Initializing DrawerViewModel to manage app settings (theme, location, etc.)
            val drawerViewModel: DrawerViewModel = viewModel()

            // Collect dark mode state from DataStore
            // This makes theme independent of system settings and persists across app restarts
            val darkModeEnabled by drawerViewModel.darkModeEnabled.collectAsState()

            // Apply theme based on user's preference from drawer toggle, not *system settings*
            CitiWayTheme(darkTheme = darkModeEnabled) {
                // Setting up navigation
                val navController = rememberNavController()

                CitiWayApp(navController)
            }
        }
    }
}

@Composable
fun CitiWayApp(navController: NavHostController, startRoute: String = HOME_ROUTE) {
    // No more drawerState needed. The menu is now part of TopBar

    //    NavStackLogger(navController)

    // Direct navigation setup - no drawer wrapper needed
    SetupNavGraph(navController, startRoute)
}