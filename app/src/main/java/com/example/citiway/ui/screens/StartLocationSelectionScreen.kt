package com.example.citiway.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import android.Manifest
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import android.location.Geocoder
import kotlinx.coroutines.launch
import java.util.Locale

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

    /*
     * Places API State Management:
     * predictions - List of autocomplete suggestions from Google Places API
     * showPredictions - Controls visibility of the dropdown suggestion list
     * These update in real-time as the user types in the search field
     */
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var showPredictions by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Starting with Cape Town as our default location on the map
    val capeTownLocation = LatLng(-33.9249, 18.4241)

    /*
     * Google Places API Setup:
     * placesClient - Main interface for Google Places API calls
     * geocoder - Android's built-in service for converting coordinates to addresses
     * Both are remembered to avoid recreation on recomposition
     */
    val placesClient = remember { Places.createClient(context) }
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

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
     * Places Search Functionality:
     * This function handles real-time search as the user types.
     * Key features:
     * 1. Only searches after 2+ characters to avoid too many API calls
     * 2. LocationBias restricts results to Cape Town area for relevance
     * 3. setCountries("ZA") ensures only South African locations appear
     * 4. Uses session tokens for billing optimization
     * 5. Automatically shows/hides dropdown based on results
     */
    fun searchPlaces(query: String) {
        if (query.length > 2) {
            val token = AutocompleteSessionToken.newInstance()
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .setLocationBias(com.google.android.libraries.places.api.model.RectangularBounds.newInstance(
                    LatLng(-34.3, 18.0), // Southwest Cape Town area
                    LatLng(-33.5, 18.9)  // Northeast Cape Town area
                ))
                .setCountries("ZA") // Restrict to South Africa
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    predictions = response.autocompletePredictions
                    showPredictions = true
                }
                .addOnFailureListener { exception ->
                    predictions = emptyList()
                    showPredictions = false
                }
        } else {
            predictions = emptyList()
            showPredictions = false
        }
    }

    /*
     * Place Selection Handler:
     * When user taps on a search suggestion, this function:
     * 1. Fetches detailed place information including coordinates
     * 2. Updates the search text with a clean location name
     * 3. Sets the marker position on the map
     * 4. Moves camera to the selected location
     * 5. Hides the suggestions dropdown
     *
     * We only request essential fields to minimize API costs and data usage
     */
    fun selectPlace(prediction: AutocompletePrediction) {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val request = FetchPlaceRequest.newInstance(prediction.placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                place.latLng?.let { latLng ->
                    selectedLocation = latLng
                    searchText = place.name ?: prediction.getPrimaryText(null).toString()
                    showPredictions = false

                    // Move camera to selected location with street-level zoom
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                    )
                }
            }
            .addOnFailureListener { exception ->
                // Handle error silently - user can try again or use map tap
            }
    }

    /*
     * Reverse Geocoding Functionality:
     * Converts map tap coordinates back into human-readable addresses.
     * This runs in a coroutine scope to avoid blocking the UI thread.
     * If geocoding fails, we fall back to a generic "Selected Location" text.
     * This provides immediate feedback when users tap anywhere on the map.
     */
    fun reverseGeocode(latLng: LatLng) {
        scope.launch {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    searchText = address.getAddressLine(0) ?: "Selected Location"
                }
            } catch (e: Exception) {
                searchText = "Selected Location"
            }
        }
    }

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
             * 5. Reverse geocode to show user their current address
             * 6. Move camera to user's position with 15f zoom (street level view)
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

                        // Show user their current address in search field
                        reverseGeocode(currentLatLng)

                        // Moving camera to user's location with a closer zoom level
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                        )
                    }
                }
        } catch (e: SecurityException) {
            // Handling cases where location access is denied  ** WIP, will do.
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
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        /*
         * Enhanced Search Field with Autocomplete:
         * This search field now provides real-time suggestions as users type.
         * Key improvements:
         * 1. IconButton makes search icon clickable for manual search
         * 2. singleLine prevents unwanted line breaks
         * 3. KeyboardOptions sets IME action to "Search"
         * 4. KeyboardActions handles "Enter" key press
         * 5. onValueChange triggers search automatically as user types
         */
        Column {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    searchPlaces(it)
                },
                placeholder = {
                    Text("Search for your location", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 14.sp)
                },
                trailingIcon = {
                    IconButton(onClick = { searchPlaces(searchText) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                singleLine = true, // Prevents new lines when user hits Enter
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search // Shows "Search" instead of "Enter" on keyboard. Better UI
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        searchPlaces(searchText) // Handles Enter key press
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            /*
             * Search Suggestions Dropdown:
             * This card appears below the search field when suggestions are available.
             * Features:
             * 1. LazyColumn for efficient scrolling of large suggestion lists
             * 2. heightIn limits dropdown size to prevent screen overflow
             * 3. Clean text formatting - only shows relevant address parts
             * 4. Clickable items that trigger place selection
             * 5. Dividers between items for better visual separation
             * 6. Card elevation provides visual hierarchy over map
             */
            if (showPredictions && predictions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(predictions) { prediction ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectPlace(prediction) }
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = prediction.getPrimaryText(null).toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                // Show simplified secondary text - removes postal codes for cleaner display
                                val secondaryText = prediction.getSecondaryText(null).toString()
                                val cleanText = secondaryText.split(",").take(2).joinToString(", ") // Only take first 2 parts
                                Text(
                                    text = cleanText,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            if (prediction != predictions.last()) {
                                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Location permission section - showing different states and providing access to location services
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
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
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )

            // Location button - only shown when permission hasn't been granted
            if (!locationPermissionState.status.isGranted) {
                TextButton(
                    onClick = { locationPermissionState.launchPermissionRequest() }
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Use my location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Use my location",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        /*
         * Enhanced Google Maps Integration:
         * The map now supports both manual selection and search-based location setting.
         * New functionality:
         * 1. onMapClick now triggers reverse geocoding to show address in search field
         * 2. Automatically hides search suggestions when map is tapped
         * 3. Maintains all existing features: zoom controls, location services, markers
         *
         * User interaction flows:
         * - Tap map → marker moves, address appears in search field
         * - Search and select → marker moves to searched location
         * - Use current location → marker shows user's position with address
         * TO DO:
         * - Need to show loading states for location services and reverse geocoding during API calls.
         * - UX currently a bit janky and slow.
         */
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Taking up remaining space in the column
                .padding(horizontal = 16.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                // When user taps the map, set location and get address
                selectedLocation = latLng
                reverseGeocode(latLng)
                showPredictions = false // Hide search suggestions when map is used
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
                    containerColor = if (selectedLocation != null) MaterialTheme.colorScheme.primary else Color.Gray
                ),
                shape = RoundedCornerShape(25.dp),
                enabled = selectedLocation != null
            ) {
                Text(
                    text = if (selectedLocation != null) "Confirm Location" else "Select Location",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}