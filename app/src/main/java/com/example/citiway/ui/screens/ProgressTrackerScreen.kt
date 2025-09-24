package com.example.citiway.ui.screens


// HomeScreen.kt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.citiway.ui.navigation.BottomNavigationBar

@Composable
fun ProgressTrackerScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) }) { paddingValues ->
        // TODO: Make JourneySelectionScreen
        paddingValues
    }
}
