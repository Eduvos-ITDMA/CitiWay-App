package com.example.citiway.core.ui.components

import com.example.citiway.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingFlat
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Custom composable that displays the route, date, and duration of a completed journey in a card format.
 *
 * This component is designed to be used within a [RowScope], allowing it to be
 * weighted if multiple composables are displayed in a row.
 *
 * @param route A string representing the journey's route (e.g., "City A to City B").
 * @param date The [LocalDate] when the journey was completed.
 * @param durationMin The duration of the journey in minutes.
 * @param weight The layout weight of this card within a [RowScope]. Defaults to 1f.
 */
@Composable
fun RowScope.CompletedJourneyCard(
    route: String,
    date: String,
    mode: String,
    durationMin: Int,
    weight: Float = 1f,
    modifier: Modifier = Modifier
) {
    val formattedDate = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

    val hours = durationMin / 60
    val minutes = durationMin % 60

    val durationText = buildString {
        if (hours > 0) {
            append("${hours}hr ")
        }
        append("${minutes}min")
    }.trim()

    val normalizedMode = mode.ifEmpty { "Mode" }

    // Determine which icon to show based on mode
    val modeIcon = when (normalizedMode.lowercase()) {
        "train" -> R.drawable.ic_train
        "bus" -> R.drawable.ic_bus
        "multi" -> R.drawable.ic_multimodal
        else -> R.drawable.ic_multimodal // Default fallback
    }

    // Parse route into start and end locations
    val routeParts = route.split("|")
    val rawStartLocation = routeParts.getOrNull(0)?.trim() ?: "Start"
    val rawEndLocation = routeParts.getOrNull(1)?.trim() ?: "End"

    // Calculate approximate character space available (accounting for arrow and spacing)
    // Rough estimate: 45 total chars for both locations combined
    val totalAvailableChars = 45
    val totalLength = rawStartLocation.length + rawEndLocation.length

    val startLocation: String
    val endLocation: String

    if (totalLength <= totalAvailableChars) {
        // Both fit comfortably - keep both as-is
        startLocation = rawStartLocation
        endLocation = rawEndLocation
    } else {
        // Need to trim - prioritize start location, try removing "Station" from end first
        val endWithoutStation = rawEndLocation
            .replace(" Station", "", ignoreCase = true)
            .replace("Station", "", ignoreCase = true)
            .trim()

        val newTotalLength = rawStartLocation.length + endWithoutStation.length

        if (newTotalLength <= totalAvailableChars) {
            // Removing "Station" is enough
            startLocation = rawStartLocation
            endLocation = endWithoutStation
        } else {
            // Still too long - need to truncate start location
            // Give end location ~15 chars, rest to start
            val endMaxChars = 15
            val startMaxChars = totalAvailableChars - endMaxChars

            startLocation = if (rawStartLocation.length > startMaxChars) {
                rawStartLocation.take(startMaxChars - 3) + "..."
            } else {
                rawStartLocation
            }

            // For end location: try without Station first, then truncate if still needed
            endLocation = if (endWithoutStation.length <= endMaxChars) {
                endWithoutStation
            } else {
                // If even without "Station" it's too long, just truncate (no ellipsis on end)
                endWithoutStation.take(endMaxChars)
            }
        }
    }

    // ========== Component composable ==========
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = modifier
            .weight(weight)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = startLocation,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Visible
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingFlat,
                        contentDescription = "to",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = endLocation,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Visible
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