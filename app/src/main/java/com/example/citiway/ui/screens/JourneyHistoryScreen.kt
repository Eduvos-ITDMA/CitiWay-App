package com.example.citiway.ui.screens


// HomeScreen.kt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.citiway.ui.navigation.BottomNavigationBar
import com.example.citiway.ui.navigation.Screen

@Composable
fun JourneyHistoryScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) }) { paddingValues ->
        // TODO: Make JourneySelectionScreen
        paddingValues
    }
}