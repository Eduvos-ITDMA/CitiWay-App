package com.example.citiway.features.destination_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.features.shared.LocationSelectionActions
import com.example.citiway.features.shared.LocationSelectionState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationSelectionContent(
    paddingValues: PaddingValues,
    state: LocationSelectionState,
    actions: LocationSelectionActions,
    cameraPositionState: CameraPositionState,
    onConfirmLocation: (LatLng) -> Unit
) {
    /* These state variables hold the current, up-to-date data for the UI, ensuring
    * the screen automatically refreshes whenever the data in the ViewModel changes.
    *    searchText - text query the user types
    *    userLocation - LatLng value of user's current location
    *    predictions - List of autocomplete suggestions from Google Places API
    *    showPredictions - Controls visibility of the dropdown suggestion list
    */
    val searchText = state.searchText
    val selectedLocation = state.selectedLocation
    val userLocation = state.userLocation
    val predictions = state.predictions
    val showPredictions = state.showPredictions
    val isLocationPermissionGranted = state.isLocationPermissionGranted

    // ========= CONTENT ===========
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
    ) {
        Title("Where are you?")

        Spacer(modifier = Modifier.height(16.dp))

        /*
         * Search bar:
         * 1. IconButton makes search icon clickable for manual search
         * 2. singleLine prevents unwanted line breaks
         * 3. KeyboardOptions sets IME action to "Search"
         * 4. KeyboardActions handles "Enter" key press
         * 5. onValueChange triggers search automatically as user types
         */
        Column {
            OutlinedTextField(
                value = state.searchText,
                onValueChange = { query ->
                    actions.setSearchText(query)
                    actions.searchPlaces(query)
                },
                placeholder = {
                    Text(
                        "Search for your location",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { actions.searchPlaces(state.searchText) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        actions.searchPlaces(searchText)
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
                                    .clickable { actions.selectPlace(prediction) }
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
                                val cleanText = secondaryText.split(",").take(2)
                                    .joinToString(", ") // Only take first 2 parts
                                Text(
                                    text = cleanText,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            if (prediction != predictions.last()) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.1f
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        VerticalSpace(14)

        /*
         * onMapClick triggers reverse geocoding to show address in search field and
         * Automatically hides search suggestions when map is tapped
         *
         * TODO:
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
                actions.setSelectedLocation(latLng)
                actions.reverseGeocode(latLng)
                actions.toggleShowPredictions(false)
            },
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = isLocationPermissionGranted
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = isLocationPermissionGranted
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

        VerticalSpace(16)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center // Centering the button horizontally
        ) {
            Button(
                onClick = {
                    selectedLocation?.let { location ->
                        onConfirmLocation(location)
                    }
                },
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

        VerticalSpace(16)
    }
}