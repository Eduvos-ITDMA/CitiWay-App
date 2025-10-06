package com.example.citiway.features.journey_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.citiway.core.ui.components.CompletedJourneyCardWithButton
import com.example.citiway.core.ui.components.Heading
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.data.local.CompletedJourney

@Composable
fun JourneyHistoryContent(
    journeys: List<CompletedJourney>,
    paddingValues: PaddingValues,
    onJourneyClick: ((String) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        Heading("Routes History:")
        VerticalSpace(16)

        journeys.forEach { journey ->
            key(journey.id) {
                VerticalSpace(12)

                CompletedJourneyCardWithButton(
                    route = journey.route,
                    date = journey.date,
                    durationMin = journey.durationMin,
                    icon = { } // No icon needed based on your screenshot
                )
            }
        }
    }
}