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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citiway.R
import com.example.citiway.core.ui.components.CompletedJourneyCardWithButton
import com.example.citiway.core.ui.components.Heading
import com.example.citiway.core.ui.components.LocationSearchField
import com.example.citiway.core.ui.components.Space
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.data.local.CompletedJourney
import com.example.citiway.features.shared.CompletedJourneysState

@Composable
fun HomeContent(
    completedJourneysState: CompletedJourneysState,
    paddingValues: PaddingValues,
    actions: HomeActions
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection()
        VerticalSpace(24)

        DestinationSearchBar(actions)
        VerticalSpace(24)

        CompletedTripsSection(
            completedJourneysState.recentJourneys,
            actions.onToggleFavourite,
            "Recent Trips"
        )
        VerticalSpace(24)

        CompletedTripsSection(
            completedJourneysState.favouriteJourneys,
            actions.onToggleFavourite,
            "Favourite Trips"
        )
        VerticalSpace(24)

        SchedulesLink(actions.onSchedulesLinkClick)
        Space(1f)
    }
}

@Composable
private fun HeaderSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Title("Hi, Commuter 👋")
        VerticalSpace(4)
        Text(
            text = "Where would you like to go today?",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun DestinationSearchBar(actions: HomeActions) {
    LocationSearchField(
        icon = { modifier ->
            Icon(
                painterResource(R.drawable.ic_map),
                contentDescription = "Open Map",
                modifier = modifier.clickable { actions.onMapIconClick() }
            )
        },
        onSelectPrediction = actions.onSelectPrediction,
        placeholder = "Where to?"
    )
}

@Composable
fun CompletedTripsSection(
    journeys: List<CompletedJourney>,
    onToggleFavourite: (String) -> Unit,
    title: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Heading(title)

        VerticalSpace(12)

        journeys.forEach { journey ->
            key(journey.id) { // DO NOT REMOVE THIS. Recomposition will not be triggered without this
                VerticalSpace(15)

                CompletedJourneyCardWithButton(
                    route = journey.route,
                    date = journey.date,
                    durationMin = journey.durationMin,
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