package com.example.citiway.features.journey_history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.citiway.core.ui.components.CompletedJourneyCardWithButton
import com.example.citiway.core.ui.components.JourneyActionDialog
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.data.local.JourneyOverview
import com.example.citiway.features.shared.CompletedJourneysActions

@Composable
fun JourneyHistoryContent(
    journeys: List<JourneyOverview>,
    paddingValues: PaddingValues,
    actions: CompletedJourneysActions
) {

    var selectedJourney by remember { mutableStateOf<JourneyOverview?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Title("Routes History:")
        VerticalSpace(16)

        journeys.forEach { journey ->
            key(journey.id) {
                VerticalSpace(12)

                CompletedJourneyCardWithButton(
                    journey = journey,
                    outlined = true,
                    icon = { modifier ->
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View journey details",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = modifier.clickable { selectedJourney = journey }
                        )
                    },
                    onCardClick = { selectedJourney = journey }
                )
            }
        }
    }

    selectedJourney?.let { journey ->
        JourneyActionDialog(
            onDismiss = { selectedJourney = null },
            onViewSummary = {
                actions.onViewJourneySummary(journey.id)
                selectedJourney = null
            },
            onStartJourney = {
                actions.onRepeatJourney(journey.startLocationLatLng, journey.destinationLatLng)
                selectedJourney = null
            }
        )
    }
}
