package com.example.citiway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.citiway.ui.navigation.SetupNavGraph
import com.example.citiway.ui.theme.CitiWayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
fun CitiWayApp(navController: NavHostController) {
    SetupNavGraph(navController = navController)
}

@Preview
@Composable
fun PreviewApp() {
    CitiWayTheme {
        CitiWayApp(navController = rememberNavController())
    }
}