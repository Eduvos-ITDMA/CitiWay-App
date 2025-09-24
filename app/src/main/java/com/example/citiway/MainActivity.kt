package com.example.citiway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.citiway.ui.theme.CitiWayTheme
import com.example.citiway.ui.navigation.setupNavGraph

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
    // Set up navigation
    val navController = rememberNavController()
    setupNavGraph(navController = navController)
}