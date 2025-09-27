package com.example.citiway.ui.screens


// HomeScreen.kt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.citiway.ui.navigation.routes.Screen

@Composable
fun DestinationSelectionScreen(navController: NavController, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Destination Selection Screen", fontSize = 24.sp)
        // TODO: Implement navigation
        Button(onClick = { navController.navigate(Screen.StartLocationSelection.route) }) {
            Text("Start Location Selection Screen")
        }
    }
}
