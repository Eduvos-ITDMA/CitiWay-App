package com.example.citiway.ui.screens

// StartLocationSelectionScreen.
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import android.Manifest
import androidx.compose.material.icons.filled.LocationOn
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun StartLocationSelectionScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    onConfirmLocation: (LatLng) -> Unit = {}
) {
    // Setting up our state variables - these will track changes during user interaction
    var searchText by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val context = LocalContext.current
    // Starting with Cape Town as our default location on the map
    val capeTownLocation = LatLng(-33.9249, 18.4241)

    // Setting up the camera position state - this controls what the user sees on the map
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(capeTownLocation, 12f)
    }

    /*
     * Location Permission Management:
     * We're using Accompanist permissions library to handle location access.
     * This creates a permission state that tracks whether the user has granted
     * ACCESS_FINE_LOCATION permission. The state automatically updates when
     * permission status changes, triggering recomposition.
     */
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    /*
     * Getting User's Current Location:
     * This function uses Google Play Services' FusedLocationProviderClient
     * to get the device's last known location. It's more efficient than GPS
     * as it uses cached location data from network/GPS/sensors.
     */
    fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            /*
             * Location Retrieval Process:
             * 1. lastLocation gives us cached location (faster than real-time GPS)
             * 2. addOnSuccessListener handles async response when location is found
             * 3. We convert Android Location to Google Maps LatLng format
             * 4. Update both userLocation (for tracking) and selectedLocation (for UI)
             * 5. Move camera to user's position with 15f zoom (street level view)
             *
             * Note: Location data is NOT saved to device storage - it's only kept
             * in memory during this screen session for privacy reasons.
             */
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        userLocation = currentLatLng
                        selectedLocation = currentLatLng

                        // Moving camera to user's location with a closer zoom level
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                        )
                    }
                }
        } catch (e: SecurityException) {
            // Handling cases where location access is denied
        }
    }

    /*
     * Permission Status Monitoring:
     * LaunchedEffect with locationPermissionState.status as key means this block
     * runs whenever permission status changes (granted/denied/requested).
     * This ensures we automatically get location as soon as user grants permission.
     */
    LaunchedEffect(locationPermissionState.status) {
        when {
            locationPermissionState.status.isGranted -> {
                getCurrentLocation()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Screen title - asking user where they are
        Text(
            text = "Where are you?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar for location input - currently just for visual, search functionality can be added later
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = {
                Text("Search for your location", color = Color.Gray, fontSize = 14.sp)
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                focusedBorderColor = Color(0xFF2196F3)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location permission section - showing different states and providing access to location services
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Displaying different messages based on permission status and location availability
            Text(
                text = when {
                    locationPermissionState.status.isGranted && userLocation != null ->
                        "📍 Current location found"
                    locationPermissionState.status.isGranted ->
                        "📍 Getting your location..."
                    else -> "📍 Tap map to select your location"
                },
                color = Color(0xFF2196F3),
                fontSize = 14.sp
            )

            // Location button
            if (!locationPermissionState.status.isGranted) {
                TextButton(
                    onClick = { locationPermissionState.launchPermissionRequest() }
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Use my location", tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Use my location", color = Color(0xFF2196F3), fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        /*
         * Google Maps Integration:
         * - cameraPositionState: Controls map view (zoom, center point)
         * - onMapClick: Captures user taps and converts screen coordinates to LatLng
         * - MapProperties: Controls map behavior (location enabled, map type)
         * - MapUiSettings: Controls UI elements (zoom controls, my location button)
         *
         * The map is taking up remaining space with weight(1f) so it expands
         * to fill available screen real estate between other UI elements.
         */
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Taking up remaining space in the column
                .padding(horizontal = 16.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                // When user taps the map, we're setting that as their selected location
                selectedLocation = latLng
            },
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = locationPermissionState.status.isGranted
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = locationPermissionState.status.isGranted
            )
        ) {
            // Showing marker for selected location if one exists
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = if (location == userLocation) "Your Current Location" else "Selected Location"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm button section - centered and styled nicely
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // Centering the button horizontally
        ) {
            Button(
                onClick = { selectedLocation?.let { onConfirmLocation(it) } },
                modifier = Modifier
                    .fillMaxWidth(0.6f) // Using 60% of available width for better proportions
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedLocation != null) Color(0xFF2196F3) else Color.Gray
                ),
                shape = RoundedCornerShape(25.dp),
                enabled = selectedLocation != null
            ) {
                Text(
                    text = if (selectedLocation != null) "Confirm Location" else "Select a location first",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}