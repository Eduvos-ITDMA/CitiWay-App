package com.example.citiway.features.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.citiway.core.ui.components.CompletedJourneyCardWithButton
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.data.local.CompletedJourney

@Composable
fun FavouritesContent(
    journeys: List<CompletedJourney>,
    paddingValues: PaddingValues,
    onToggleFavourite: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        Title("Favourite Trips")
        VerticalSpace(16)

        journeys.forEach { journey ->
            key(journey.id) {
                VerticalSpace(12)

                CompletedJourneyCardWithButton(
                    route = journey.route,
                    date = journey.date,
                    durationMin = journey.durationMin,
                    mode = journey.mode,
                    outlined = false,
                    icon = { modifier ->
                        Icon(
                            imageVector = if (journey.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (journey.isFavourite) "Remove from favourites" else "Add to favourites",
                            tint = if (journey.isFavourite) Color.Red else MaterialTheme.colorScheme.onPrimary,
                            modifier = modifier.clickable { onToggleFavourite(journey.id) }
                        )
                    }
                )
            }
        }
    }
}