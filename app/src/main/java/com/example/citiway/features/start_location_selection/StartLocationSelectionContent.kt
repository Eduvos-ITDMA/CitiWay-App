package com.example.citiway.features.start_location_selection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.citiway.core.ui.components.HorizontalSpace
import com.example.citiway.core.ui.components.LocationSearchField
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.features.shared.MapActions
import com.example.citiway.features.shared.MapState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun StartLocationSelectionContent(
    paddingValues: PaddingValues,
    state: MapState,
    actions: MapActions,
    placesState: PlacesState,
    placesActions: PlacesActions,
    onPermissionRequest: () -> Unit,
    cameraPositionState: CameraPositionState,
    onConfirmLocation: (SelectedLocation) -> Unit,
    locationEnabledInApp: Boolean,

    // NEW PARAMETERS:
    isLocationPermissionGranted: Boolean,
    onEnableLocation: () -> Unit,
    onRequestSystemPermission: () -> Unit
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
            placesState = placesState,
            placesActions = placesActions,
            placeholder = "Where are you?"
        )


        // Location button - hide only when BOTH permission is granted AND enabled in app
        if (!(isLocationPermissionGranted && locationEnabledInApp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = {
                        when {
                            // Case 1: Has system permission but app preference is disabled
                            // Just enable the app preference, no need to request permission again
                            isLocationPermissionGranted && !locationEnabledInApp -> {
                                onEnableLocation()  // Using the callback, and not drawerViewModel*
                                // Location will be fetched by the LaunchedEffect automatically
                            }

                            // Case 2: No system permission at all
                            // Request the system permission (Android popup will appear)
                            !isLocationPermissionGranted -> {
                                onRequestSystemPermission()  // Using the callback to routee
                            }

                            // Case 3: Both are already enabled (shouldn't reach here due to if condition)
                            else -> {
                                // Do nothing, button shouldn't be visible anyway
                            }
                        }
                    },
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
                .weight(1f)
                .padding(horizontal = 16.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = actions.selectLocationOnMap,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                // Enable blue dot only when BOTH conditions are true
                isMyLocationEnabled = isLocationPermissionGranted && locationEnabledInApp
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                // Match the isMyLocationEnabled condition
                myLocationButtonEnabled = isLocationPermissionGranted && locationEnabledInApp
            )
        ) {
            // Only show marker when we're NOT showing the blue location dot
            // If the selected location IS the user location and we have permissions,
            // the blue dot will show it, so don't duplicate with a marker
            val isShowingUserLocationWithBlueDot =
                isLocationPermissionGranted &&
                        locationEnabledInApp &&
                        selectedLocation?.latLng == userLocation

            // Then in your code:
            if (selectedLocation != null && !isShowingUserLocationWithBlueDot) {
                Marker(
                    state = rememberMarkerState(position = selectedLocation.latLng),
                    title = "Selected Location"
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