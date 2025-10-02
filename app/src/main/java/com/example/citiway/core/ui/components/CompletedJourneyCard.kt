package com.example.citiway.core.ui.components

import com.example.citiway.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun RowScope.CompletedJourneyCard(route: String, date: LocalDate, durationMin: Int, weight: Float = 1f) {
    val formattedDate = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

    val hours = durationMin / 60
    val minutes = durationMin % 60

    val durationText = buildString {
        if (hours > 0) {
            append("${hours}hr ")
        }
        append("${minutes}min")
    }.trim()

    // ========== Component composable ==========
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.weight(weight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // ========== Route text ==========
                Text(
                    text = route,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ========== Date and Duration ==========
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
                    Spacer(modifier = Modifier.width(18.dp))

                    // ========== Duration Icon and Text ==========
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
            }
        }
    }
}
