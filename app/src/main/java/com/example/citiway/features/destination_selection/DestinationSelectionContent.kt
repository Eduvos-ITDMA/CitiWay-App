package com.example.citiway.features.destination_selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationSelectionContent(
    navController: NavController,
    paddingValues: PaddingValues,
) {
    // Basic state for destination input - no functionality yet, just UI
    var destinationText by remember { mutableStateOf("") }

    // Starting with Cape Town as our default location on the map
    val capeTownLocation = LatLng(-33.9249, 18.4241)

    // Setting up the camera position state - this controls what the user sees on the map
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(capeTownLocation, 12f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Screen title - asking user where they want to go
        Text(
            text = "Where do you want to go?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar for destination input - currently just for visual, no functionality
        OutlinedTextField(
            value = destinationText,
            onValueChange = { destinationText = it },
            placeholder = {
                Text("Search for your destination", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 14.sp)
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Destination info section - placeholder text for now
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📍 Tap map to select your destination",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Taking up remaining space in the column
                .padding(horizontal = 16.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                // When user taps the map - no functionality for now
            },
            properties = MapProperties(
                mapType = MapType.NORMAL
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true
            )
        ) {
            // No markers for now - just the map
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm button section - always enabled to navigate to start screen
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // Centering the button horizontally
        ) {
            Button(
                onClick = {navController.navigate(Screen.StartLocationSelection.route)}, // This will navigate to start location screen
                modifier = Modifier
                    .fillMaxWidth(0.6f) // Using 60% of available width for better proportions
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary // Always enabled for now
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Confirm Destination",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}