package com.example.citiway.core.ui.components

import com.example.citiway.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.citiway.data.local.JourneyOverview
import java.time.format.DateTimeFormatter

/**
 * Custom composable that displays the route, date, and duration of a completed journey in a card format.
 *
 * This component is designed to be used within a [RowScope], allowing it to be
 * weighted if multiple composables are displayed in a row.
 *
 * @param journey The JourneyOverview object which contains all journey data for display
 * @param weight The layout weight of this card within a [RowScope]. Defaults to 1f.
 */
@Composable
fun RowScope.CompletedJourneyCard(
    journey: JourneyOverview,
    onCardClick: (JourneyOverview) -> Unit,
    weight: Float = 1f
) {
    val formattedDate = journey.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

    val hours = journey.durationMin / 60
    val minutes = journey.durationMin % 60

    val durationText = buildString {
        if (hours > 0) {
            append("${hours}hr ")
        }
        append("${minutes}min")
    }.trim()

    val normalizedMode = journey.mode.ifEmpty { "Mode" }

    // Determine which icon to show based on mode
    val modeIcon = when (normalizedMode.lowercase()) {
        "train" -> R.drawable.ic_train
        "bus" -> R.drawable.ic_bus
        "multi" -> R.drawable.ic_multimodal
        else -> R.drawable.ic_multimodal // Default fallback
    }

    // Parse route into start and end locations
    val routeParts = journey.route.split("|")
    val (startLocation, endLocation) = getDisplayNames(
        routeParts.getOrNull(0) ?: "Start",
        routeParts.getOrNull(1) ?: "End"
    )


    // ========== Component composable ==========
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.weight(weight),
        onClick = { onCardClick(journey) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {

                // ========== Route text with arrow icon ==========
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = startLocation,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(1.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingFlat,
                        contentDescription = "to",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(1.dp))

                    Text(
                        text = endLocation,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // ========== Date and Duration ==========
                FlowRow(
                    itemVerticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Item 1: Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedDate,
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Item 2: Duration
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = durationText,
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    // Item 3: Mode of Transport
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(modeIcon),
                            contentDescription = normalizedMode,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = normalizedMode,
                            color = MaterialTheme.colorScheme.background,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


private fun getDisplayNames(locationText1: String, locationText2: String): Array<String> {
    val lt1Parts = locationText1.split(",")
    val lt2Parts = locationText2.split(",")

    val lt1Area = lt1Parts.last()
    val lt2Area = lt2Parts.last()

    return if (lt1Area == lt2Area) {
        arrayOf(
            removeStartingNumberAndSpace(lt1Parts.first()),
            removeStartingNumberAndSpace(lt2Parts.first())
        )
    } else {
        arrayOf(lt1Area, lt2Area)
    }
}

private fun removeStartingNumberAndSpace(input: String): String {
    val regex = Regex("^\\d+\\s")
    return input.replaceFirst(regex, "")
}