package com.example.citiway.features.start_location_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.citiway.core.ui.components.HorizontalSpace
import com.example.citiway.core.ui.components.LocationSearchField
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.core.util.StartLocationScreenPreview
import com.example.citiway.features.shared.LocationSelectionActions
import com.example.citiway.features.shared.LocationSelectionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import com.example.citiway.features.shared.MapActions
import com.example.citiway.features.shared.MapState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun StartLocationSelectionContent(
    paddingValues: PaddingValues,
    state: MapState,
    actions: MapActions,
    onPermissionRequest: () -> Unit,
    cameraPositionState: CameraPositionState,
    onConfirmLocation: (LatLng) -> Unit,
    locationEnabledInApp: Boolean
) {
    // State Variables from StartLocationViewModel. Same as in destination_selection
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
        Title("Enter Location")

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
            state = state,
            actions = actions,
            onSelectPrediction = actions.selectPlace, // JourneyViewModel must decide what to do here
            placeholder = "Where are you?"
        )

        // Location button - only shown when BOTH permissions aren't granted
        if (!isLocationPermissionGranted || !locationEnabledInApp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = onPermissionRequest,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Use my location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    HorizontalSpace(4)
                    Text(
                        text = "Use my location",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
        VerticalSpace(8)
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when {
                    // BOTH must be true AND location found
                    isLocationPermissionGranted && locationEnabledInApp && userLocation != null ->
                        Icons.Default.MyLocation
                    // BOTH are true but still loading location
                    isLocationPermissionGranted && locationEnabledInApp && userLocation == null ->
                        Icons.Default.Autorenew
                    // Either permission is missing
                    else -> Icons.Default.TouchApp
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            HorizontalSpace(4)
            Text(
                text = when {
                    // BOTH must be true AND location found
                    isLocationPermissionGranted && locationEnabledInApp && userLocation != null ->
                        "Current location found"
                    // BOTH are true but still loading location
                    isLocationPermissionGranted && locationEnabledInApp && userLocation == null ->
                        "Getting your location..."
                    // Either permission is missing
                    else -> "Tap map to select location"
                },
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = TextDecoration.Underline
            )
        }

        VerticalSpace(10)

        /*
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
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        VerticalSpace(16)
    }
}