package com.example.citiway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.citiway.ui.theme.CitiWayTheme
import com.example.citiway.ui.screens.SplashScreen
import com.example.citiway.ui.screens.HomeScreen
import com.example.citiway.ui.screens.MapScreen
import com.example.citiway.ui.screens.FavoritesScreen
import com.example.citiway.ui.screens.SettingsScreen
import com.example.citiway.ui.navigation.BottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CitiWayTheme {
                CitiWayApp()
            }
        }
    }
}

@Composable
fun CitiWayApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Only show bottom nav on main screens, not splash
            if (currentRoute != "splash") {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("map") { MapScreen(navController) }
            composable("favorites") { FavoritesScreen(navController) }
            composable("settings") { SettingsScreen(navController) }

            /* Not in Nav, will come later. These are "flow" screens - you navigate through them in a sequence using buttons
            composable("location") { LocationScreen(navController) }
            composable("journey") { JourneyScreen(navController) }
            composable("progress") { ProgressScreen(navController) }
            composable("summary") { SummaryScreen(navController) }
             */
        }
    }
}