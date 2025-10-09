package com.example.citiway.features.destination_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citiway.core.ui.components.LocationSearchField
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.features.shared.MapActions
import com.example.citiway.features.shared.MapState
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
    state: MapState,
    actions: MapActions,
    placesState: PlacesState,
    placesActions: PlacesActions,
    cameraPositionState: CameraPositionState,
    onConfirmLocation: (SelectedLocation) -> Unit
) {
    val selectedLocation = state.selectedLocation
    val userLocation = state.userLocation
    val isLocationPermissionGranted = state.isLocationPermissionGranted

    // ========= CONTENT ===========
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
    ) {
        Title("Enter Destination")

        VerticalSpace(16)

        LocationSearchField(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            },
            placesState = placesState,
            placesActions = placesActions,
            placeholder = "Where are you going?"
        )

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
                .border(1.dp, MaterialTheme.colorScheme.onBackground),
            cameraPositionState = cameraPositionState,
            onMapClick = actions.selectLocationOnMap,
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
                    state = MarkerState(position = location.latLng),
                    title = if (location.latLng == userLocation) "Your Current Location" else "Selected Location"
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