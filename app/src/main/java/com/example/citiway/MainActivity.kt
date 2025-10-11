package com.example.citiway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.citiway.data.local.CitiWayDatabase
import com.example.citiway.core.navigation.graphs.SetupNavGraph
import com.example.citiway.core.navigation.routes.HOME_ROUTE
import com.example.citiway.core.ui.theme.CitiWayTheme
import com.example.citiway.features.shared.DrawerViewModel
import com.example.citiway.features.shared.CompletedJourneysViewModel
import com.google.android.libraries.places.api.Places
import com.citiway.data.local.DatabaseTest
import com.example.citiway.features.shared.createDummyJourneys

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        // Test the database
        DatabaseTest(this).runTest()

        val database = CitiWayDatabase.getDatabase(this)
        val dao = database.savedPlaceDao()
        createDummyJourneys(dao) // Uncomment this line to populate db with dummy data

        enableEdgeToEdge()
        setContent {
            // Initializing DrawerViewModel to manage app settings (theme, location, etc.)
            val drawerViewModel: DrawerViewModel = viewModel()

            // Collect dark mode state from DataStore
            val darkModeEnabled by drawerViewModel.darkModeEnabled.collectAsState()

            // Apply theme based on user's preference from drawer toggle
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
    SetupNavGraph(navController, startRoute)
}