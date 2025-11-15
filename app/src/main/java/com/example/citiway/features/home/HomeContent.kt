package com.example.citiway.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citiway.R
import com.example.citiway.core.ui.components.CompletedJourneyCardWithButton
import com.example.citiway.core.ui.components.ConfirmationDialog
import com.example.citiway.core.ui.components.LocationSearchField
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.core.utils.HomeScreenPreview
import com.example.citiway.data.local.CompletedJourney
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.features.shared.CompletedJourneysState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.height


@Composable
fun HomeContent(
    completedJourneysState: CompletedJourneysState,
    homeActions: HomeActions,
    placesState: PlacesState,
    placesActions: PlacesActions,
    paddingValues: PaddingValues,
    userName: String = "Commuter",
) {

    // Storing trip selection state
    var selectedTripId by remember { mutableStateOf<Int?>(null) }
    var selectedTripStartStop by remember { mutableStateOf<String?>(null) }
    var selectedTripEndStop by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection(userName) // Pass the name

        VerticalSpace(24)

        DestinationSearchBar(homeActions, placesState, placesActions)

        VerticalSpace(24)

        CompletedTripsSection(
            completedJourneysState.recentJourneys,
            homeActions.onToggleFavourite,
            "Recent Trips",
            "No recent trips",
            onTitleClick = homeActions.onRecentTitleClick,
            onJourneyClick = { journey ->  // Extracting the fields we need
                selectedTripId = journey.tripId
                selectedTripStartStop = journey.startStop
                selectedTripEndStop = journey.endStop
            }
        )

        VerticalSpace(24)

        CompletedTripsSection(
            completedJourneysState.favouriteJourneys,
            homeActions.onToggleFavourite,
            "Favourite Trips",
            "No trips saved as favourite yet",
            onTitleClick = homeActions.onFavouritesTitleClick,
            onJourneyClick = { journey ->
                selectedTripId = journey.tripId
                selectedTripStartStop = journey.startStop
                selectedTripEndStop = journey.endStop
            }
        )

        VerticalSpace(24)

        SchedulesLink(homeActions.onSchedulesLinkClick)

        VerticalSpace(18)
    }

    // When selected where to go
    // Show dialogbox when trip is selected
    if (selectedTripId != null && selectedTripStartStop != null && selectedTripEndStop != null) {
        JourneyActionDialog(
            tripId = selectedTripId!!,
            startStop = selectedTripStartStop!!,
            endStop = selectedTripEndStop!!,
            onDismiss = {
                selectedTripId = null
                selectedTripStartStop = null
                selectedTripEndStop = null
            },
            onViewSummary = { tripId ->
                homeActions.onViewJourneySummary(tripId)  // Calling the action
                selectedTripId = null
                selectedTripStartStop = null
                selectedTripEndStop = null
            },
            onStartJourney = { startStop, endStop ->
                homeActions.onStartJourney(startStop, endStop)  // Calling the action!
                selectedTripId = null
                selectedTripStartStop = null
                selectedTripEndStop = null
            }
        )
    }

}

@Composable
fun SectionTitleWithArrow( //Added small arrow so users know they can click it to see more.
    title: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier
                .size(20.dp)
                .padding(start = 6.dp)
        )
    }
}


@Composable
private fun HeaderSection(userName: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Title("Hi, $userName 👋")
        VerticalSpace(4)
        Text(
            text = "Where would you like to go today?",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun DestinationSearchBar(
    homeActions: HomeActions,
    placesState: PlacesState,
    placesActions: PlacesActions
) {
    LocationSearchField(
        icon = { modifier ->
            Icon(
                painterResource(R.drawable.ic_map),
                contentDescription = "Open Map",
                modifier = modifier.clickable { homeActions.onMapIconClick() }
            )
        },
        placesState = placesState,
        placesActions = placesActions,
        onSelectPrediction = homeActions.onSelectPrediction,
        placeholder = "Where to?"
    )
}

@Composable
fun CompletedTripsSection(
    journeys: List<CompletedJourney>,
    onToggleFavourite: (String) -> Unit,
    title: String,
    noJourneysText: String,
    onTitleClick: (() -> Unit)? = null,
    onJourneyClick: (CompletedJourney) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionTitleWithArrow(title = title, onClick = onTitleClick)

        VerticalSpace(4)

        if (journeys.isEmpty()) {
            Text(
                text = noJourneysText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        journeys.forEach { journey ->
            key(journey.id) { // DO NOT REMOVE THIS. Recomposition will not be triggered without this
                VerticalSpace(15)

                CompletedJourneyCardWithButton(
                    route = journey.route,
                    date = journey.date,
                    durationMin = journey.durationMin,
                    mode = journey.mode,
                    onClick = { onJourneyClick(journey) },
                    icon = { modifier ->
                        Icon(
                            imageVector = if (journey.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (journey.isFavourite) "Remove from favourites" else "Add to favourites",
                            tint = if (journey.isFavourite) Color.Red else MaterialTheme.colorScheme.onPrimary,
                            modifier = modifier.clickable { onToggleFavourite(journey.id) }
                        )
                    })
            }
        }
    }
}

@Composable
fun JourneyActionDialog(
    tripId: Int,  // Simply passing ID for journey retrival
    startStop: String,
    endStop: String,
    onDismiss: () -> Unit,
    onViewSummary: (Int) -> Unit,  // Callback receives ID
    onStartJourney: (String, String) -> Unit  // Callback receives locations
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.border(
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ),
        title = {
            Text(
                text = "Journey Options",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "What would you like to do with this journey?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                VerticalSpace(24)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            onViewSummary(tripId)  // Passing TripId hen button clicked
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            "View\nSummary",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }

                    Button(
                        onClick = {
                            onStartJourney(startStop, endStop)  // passing locations
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            "Start\nJourney",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun SchedulesLink(onSchedulesLinkClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(end = 12.dp)
            .clickable { onSchedulesLinkClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "See MyCiTi and Metrorail Schedules",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
fun HomePreview() {
    HomeScreenPreview()
}
