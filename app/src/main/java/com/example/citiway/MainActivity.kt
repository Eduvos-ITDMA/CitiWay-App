package com.example.citiway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.core.navigation.graphs.SetupNavGraph
import com.example.citiway.core.navigation.routes.HOME_ROUTE
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.ui.theme.CitiWayTheme
import com.example.citiway.core.utils.NavStackLogger
import com.example.citiway.features.shared.DrawerViewModel
import com.google.android.libraries.places.api.Places
import com.example.citiway.data.repository.CitiWayRepository

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

        enableEdgeToEdge()
        setContent {
            val drawerViewModel: DrawerViewModel = viewModel()
            val darkModeEnabled by drawerViewModel.darkModeEnabled.collectAsState()

            CitiWayTheme(darkTheme = darkModeEnabled) {
                val navController = rememberNavController()

                // Check if user exists to determine start screen
                var startRoute by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val hasUser = repository.hasUser()
                    startRoute = if (hasUser) {
                        HOME_ROUTE
                    } else {
                        Screen.Onboarding.route
                    }
                }

                startRoute?.let { route ->
                    CitiWayApp(navController, route)
                }
            }
        }
    }
}

@Composable
fun CitiWayApp(navController: NavHostController, startRoute: String = HOME_ROUTE) {
    NavStackLogger(navController)
    SetupNavGraph(navController, startRoute)
}