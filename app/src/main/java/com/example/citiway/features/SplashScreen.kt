package com.example.citiway.features

// SplashScreen.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SplashScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF2196F3) // Blue background like Tristan's mockup
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // CitiWay Title
            Text(
                text = "CitiWay",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700) // Golden yellow color
            ) // Tristan/Calebs color palette for acutual values

            // Subtitle
            Text(
                text = "Find the perfect route\nto your\ndestination",
                fontSize = 18.sp,
                color = Color(0xFFFFD700),
                modifier = Modifier.padding(top = 16.dp, bottom = 80.dp),
                lineHeight = 24.sp
            )

            // Start Travel Button
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700), // Golden yellow button
                    contentColor = Color(0xFF2196F3) // Blue text
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Start Travel",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}