package com.example.citiway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.core.navigation.graphs.SetupNavGraph
import com.example.citiway.core.navigation.routes.HOME_ROUTE
import com.example.citiway.core.ui.theme.CitiWayTheme
import com.example.citiway.features.shared.DrawerViewModel
import com.google.android.libraries.places.api.Places

import com.example.citiway.data.local.DatabaseSeeder
import com.example.citiway.data.repository.CitiWayRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        // Initialize database
        val database = CitiWayDatabase.getDatabase(this)
        val repository = CitiWayRepository(database)
        val seeder = DatabaseSeeder(repository)

        // Seed database on first launch
        lifecycleScope.launch {
//            seeder.seedDatabase()  // comment this line out after 1st app launch to avoid duplicated data
        }

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