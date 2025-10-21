package com.example.citiway.features.progress_tracker

import android.icu.text.DecimalFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.citiway.R
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.ui.components.HorizontalSpace
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.core.utils.ProgressTrackerScreenPreview
import com.example.citiway.core.utils.convertIsoToHhmm
import com.example.citiway.features.shared.Instruction
import com.example.citiway.features.shared.JourneyState
import com.example.citiway.features.shared.Stop

@Composable
fun ProgressTrackerContent(
    journeyState: JourneyState,
    paddingValues: PaddingValues,
    navController: NavController
) {

    // Track coordinates for progress line
    var stepCoordinates by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var boxOffset by remember { mutableStateOf(Offset.Zero) } //  Tracks the Box position, stops the bug when i scroll
    val connectorColour = MaterialTheme.colorScheme.secondary

    val journey = journeyState.journey

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Journey Progress",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )
        VerticalSpace(16)

        if (journey != null) {
            // ETA and Distance Card
            val meters = journey.distanceMeters
            val distanceText =
                if (meters >= 1000) {
                    "${DecimalFormat("0.0").format(meters / 1000.0)}km"
                } else {
                    "${meters}m"
                }

            ETACard(
                eta = convertIsoToHhmm(journey.arrivalTime.toString()),
                distance = distanceText
            )
            VerticalSpace(24)

            // Container for all progress tracker data, including connector lines
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        boxOffset = coordinates.positionInRoot()
                    }
            )
            {
                // Canvas on which to draw connector lines
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopStart) // CentreStart broke it,  goes to first circle.
                ) {
                    // Draw connecting lines between steps
                    for (i in 0 until stepCoordinates.size - 1) {
                        val start = stepCoordinates[i]
                        val end = stepCoordinates[i + 1]

                        // Only draw if coordinates are valid
                        if (start != Offset.Zero && end != Offset.Zero) {
                            val adjustedStart = Offset(
                                x = start.x - boxOffset.x,
                                y = start.y - boxOffset.y
                            )
                            val adjustedEnd = Offset(
                                x = end.x - boxOffset.x,
                                y = end.y - boxOffset.y
                            )

                            // Dashed line between steps
                            drawLine(
                                color = connectorColour,
                                start = adjustedStart,
                                end = adjustedEnd,
                                strokeWidth = 4.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
                            )
                        }
                    }
                }
                // ======================================================================
                // Currently assuming start location and destination are not also stops
                // e.g. when a user starts at a station
                // TODO: Adjust UI if start location is first stop or destination is last stop
                // ======================================================================

                Column(modifier = Modifier.fillMaxWidth()) {
                    // Start Location
                    JourneyStep(
                        isStart = true,
                        title = journeyState.startLocation?.primaryText
                            ?: "Error: Unknown Location",
                        hasEditIcon = true,
                        onCoordinatesChanged = { offset -> // Still need to figure out what's going on here
                            stepCoordinates = stepCoordinates.toMutableList().apply {
                                if (isEmpty()) add(offset) else set(0, offset)
                            }
                        }
                    )
                    VerticalSpace(12)
                    InstructionStep(journey.instructions.first())

                    // Create card for each stop with instruction below it
                    journey.stops.forEachIndexed { index, stop ->
                        TransitStopCard(stop) { offset ->
                            stepCoordinates = stepCoordinates.toMutableList().apply {
                                while (size < 2) add(Offset.Zero)
                                set(1, offset)
                            }
                        }

                        val instruction = journey.instructions[index + 1]
                        InstructionStep(instruction)
                    }

                    // End Location
                    JourneyStep(
                        isStart = false,
                        title = "Eduvos Cape Town - Mowbray",
                        hasEditIcon = true,
                        onCoordinatesChanged = { offset ->
                            stepCoordinates = stepCoordinates.toMutableList().apply {
                                while (size < 5) add(Offset.Zero)
                                set(4, offset)
                            }
                        }
                    )
                }
            }

            VerticalSpace(24)

            // Cancel Journey Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center // Centering the button horizontally
            ) {
                Button(
                    // TODO: Reckon we just go back to home screen.
                    // In future check if first stop has been reached, if so go back home, otherwise go back to journey selection
                    onClick = { navController.navigate(Screen.Home.route) },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(0.5f)
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Cancel Journey",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            VerticalSpace(24)
        }
    }
}

@Composable
fun ETACard(eta: String, distance: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ETA:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = eta,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .height(40.dp)
                    .width(2.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = distance,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun JourneyStep(
    isStart: Boolean,
    title: String,
    hasEditIcon: Boolean,
    onCoordinatesChanged: (Offset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon indicator depending on if it's the start or end. From users given info
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
                .onGloballyPositioned { coordinates ->
                    // Using positionInRoot() to get absolute position
                    val center = Offset(
                        x = coordinates.positionInRoot().x + coordinates.size.width / 2f,
                        y = coordinates.positionInRoot().y + coordinates.size.height / 2f
                    )
                    onCoordinatesChanged(center)
                },
            contentAlignment = Alignment.Center
        ) {
            if (isStart) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            MaterialTheme.colorScheme.onBackground,
                            shape = CircleShape
                        )
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_location_on),
                    contentDescription = "Destination",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        HorizontalSpace(12)

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )

                if (hasEditIcon) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit location",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InstructionStep(instruction: Instruction) {
    VerticalSpace(12)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Empty space for progress line ....
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
        )

        HorizontalSpace(12) // gap for alignment leave space for the tracker

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(modeIcon(instruction.travelMode)),
                contentDescription = "instruction",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )

            HorizontalSpace(8)

            Column {
                Text(
                    text = instruction.text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "approx. ${instruction.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
    VerticalSpace(18)
}

@Composable
fun TransitStopCard(
    stop: Stop,
    onCoordinatesChanged: (Offset) -> Unit
) {
    var routeName = stop.routeName
    var routeColor = MaterialTheme.colorScheme.secondary
    if (stop.toMode == "WALK") {
        routeColor = MaterialTheme.colorScheme.onSurfaceVariant
        routeName = "Disembark"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        // Circle indicator
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(120.dp)
                .onGloballyPositioned { coordinates ->
                    // Use positionInRoot() to get absolute position
                    val center = Offset(
                        x = coordinates.positionInRoot().x + coordinates.size.width / 2f,
                        y = coordinates.positionInRoot().y + coordinates.size.height / 2f
                    )
                    onCoordinatesChanged(center)
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(16.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }

        HorizontalSpace(12)

        Column(modifier = Modifier.weight(1f)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stop.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        VerticalSpace(2)

                        var duration = stop.nextDepartureMin
                        // "Next bus in 11min" with orange color for time
                        val annotatedString = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                when (stop.toMode) {
                                    "WALK" -> {
                                        append("Arrives in ")
                                        duration = stop.arrivesInMin
                                    }

                                    "HEAVY_RAIL" -> {
                                        append("Next train in ")
                                    }

                                    "BUS" -> {
                                        append("Next bus in ")
                                    }
                                }
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            ) {
                                append("$duration min")
                            }
                        }

                        Text(
                            text = annotatedString,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Route info section with vertical divider (The card)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                            )
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                painter = painterResource(modeIcon(stop.fromMode)),
                                contentDescription = stop.fromMode,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )

                            HorizontalSpace(8)

                            Icon(
                                painter = painterResource(R.drawable.double_arrow_right), // Downloaded icon from site fonts.google, can be customised.
                                contentDescription = "To",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(28.dp)
                            )

                            HorizontalSpace(8)

                            Icon(
                                painter = painterResource(modeIcon(stop.toMode)),
                                contentDescription = stop.toMode,
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )

                            HorizontalSpace(12)

                            // Vertical divider
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(28.dp)
                                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                            )
                        }

                        Text(
                            text = routeName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = routeColor,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

private fun modeIcon(travelMode: String?): Int {
    return when (travelMode) {
        "WALK" -> R.drawable.ic_walk
        "BUS" -> R.drawable.ic_bus
        "HEAVY_RAIL" -> R.drawable.ic_train
        else -> R.drawable.ic_question_mark
    }
}

@Preview
@Composable
fun ProgressTrackerContentPreview() {
    ProgressTrackerScreenPreview()
}