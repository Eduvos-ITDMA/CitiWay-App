package com.example.citiway.features.progress_tracker

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.citiway.R
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.ui.components.ConfirmationDialog
import com.example.citiway.core.ui.components.HorizontalSpace
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.core.utils.ProgressTrackerScreenPreview
import com.example.citiway.core.utils.convertIsoToHhmm
import com.example.citiway.features.shared.Instruction
import com.example.citiway.features.shared.JourneyState
import com.example.citiway.features.shared.Stop
import com.example.citiway.features.shared.StopType

@Composable
fun ProgressTrackerContent(
    journeyState: JourneyState,
    paddingValues: PaddingValues,
    navController: NavController,
    toggleSpeedUp: () -> Unit,
) {
    // Track coordinates for progress line
    val journey = journeyState.journey
    var stepCoordinates by remember {
        mutableStateOf(Array<Offset?>(journey?.stops?.size?.plus(1) ?: 0) { null })
    }
    var boxOffset by remember { mutableStateOf(Offset.Zero) }
    val connectorColour = MaterialTheme.colorScheme.secondary
    var showCancellationDialog by remember { mutableStateOf(false) }

    fun updateCoordinate(index: Int, offset: Offset) {
        val localOffset = offset - boxOffset
        stepCoordinates[index] = localOffset
        // Trigger recomposition when stepCoordinatesState changes
        stepCoordinates = stepCoordinates.clone()
    }

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
                distance = distanceText,
                toggleSpeedUp
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
                        .align(Alignment.TopStart)
                ) {
                    // Draw connecting lines between stops
                    for (i in stepCoordinates.dropLast(1).indices) {
                        val start = stepCoordinates[i]
                        val end = stepCoordinates[i + 1]

                        val pathEffect = if (!journey.stops[i].reached) {
                            PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
                        } else null

                        if (start != Offset.Zero && start != null && end != Offset.Zero && end != null) {
                            drawLine(
                                color = connectorColour,
                                start = start,
                                end = end,
                                strokeWidth = 4.dp.toPx(),
                                pathEffect = pathEffect
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

                    // ======================================================
                    // Start Location
                    // ======================================================
                    JourneyStep(
                        isStart = true,
                        title = journeyState.startLocation?.primaryText
                            ?: "Error: Unknown Location",
                        hasEditIcon = true,
                    ) { offset ->
                        updateCoordinate(0, offset)
                    }
                    VerticalSpace(12)
                    InstructionStep(journey.instructions.first())

                    // ======================================================
                    // Create card for each stop with instruction below it
                    // ======================================================
                    journey.stops.forEachIndexed { index, stop ->
                        // Stop
                        TransitStopCard(stop) { offset ->
                            updateCoordinate(index + 1, offset)
                        }

                        // Instruction
                        val instruction = journey.instructions[index + 1]
                        InstructionStep(instruction)
                    }

                    // ======================================================
                    // End Location
                    // ======================================================
                    JourneyStep(
                        isStart = false,
                        title = journeyState.destination?.primaryText
                            ?: "Error: Unknown Location",
                        hasEditIcon = true,
                    ) { offset ->
                        updateCoordinate(journey.stops.size, offset)
                    }

                    // Complete Journey Logic
                    if (journey.stops.last().reached){
                        navController.navigate(Screen.JourneySummary.route)
                    }
                }
            }

            VerticalSpace(24)

            // Cancel Journey Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { showCancellationDialog = true },
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

            // Confirm Cancellation Dialog
            ConfirmationDialog(
                visible = showCancellationDialog,
                title = "Are you sure you want to cancel this journey?",
                message = "This journey cannot be selected again if the first transit has already departed",
                onConfirm = {
                    if (journeyState.journey.stops[0].reached) {
                        navController.navigate(Screen.Home.route)
                    } else {
                        navController.navigate(Screen.JourneySelection.route)
                    }
                },
                onDismiss = {
                    showCancellationDialog = false
                },
            )

            VerticalSpace(24)
        }
    }
}

@Composable
fun ETACard(eta: String, distance: String, toggleSpeedUp: () -> Unit) {
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

        val buttonShape = RoundedCornerShape(25.dp)
        Button(
            onClick = toggleSpeedUp,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(50.dp)
                .padding(bottom = 7.dp)
                .align(Alignment.CenterHorizontally)
                .border(
                    BorderStroke(2.dp, MaterialTheme.colorScheme.secondary.copy(0.7f)),
                    shape = buttonShape
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary.copy(0.4f)
            ),
            shape = buttonShape,
            content = { Text("Speed Up (demo)", color = MaterialTheme.colorScheme.onBackground) }
        )
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
    VerticalSpace(16)
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
    VerticalSpace(16)
}

@Composable
fun TransitStopCard(
    stop: Stop,
    onCoordinatesChanged: (Offset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        // Node marker
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight()
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

        val iconRes = when (stop.stopType) {
            StopType.DEPARTURE -> R.drawable.ic_right_chevron
            StopType.ARRIVAL -> R.drawable.ic_left_chevron
        }

        val arrowColor = when (stop.stopType) {
            StopType.DEPARTURE -> MaterialTheme.colorScheme.secondary // Keeping yellow for boarding
            StopType.ARRIVAL -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) // Darker for disembarking
        }

        Icon(
            painter = painterResource(iconRes),
            contentDescription = "",
            modifier = Modifier
                .width(20.dp)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp, top = 8.dp, start = 0.dp, bottom = 0.dp),
            tint = arrowColor
        )

        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
            Text(
                text = when (stop.stopType) {
                    StopType.DEPARTURE -> "Board"
                    StopType.ARRIVAL -> "Disembark"
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(end = 10.dp, bottom = 2.dp)
                    .align(Alignment.End)
            )
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

                        // "Next bus in 11min" with orange color for time
                        val stopNotReached = stop.nextEventInMin != null && stop.nextEventInMin > 0
                        Log.d("Journey progress", "Stop reached in ${stop.nextEventInMin}")
                        val annotatedString = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                when (stop.stopType) {
                                    StopType.ARRIVAL -> {
                                        if (stopNotReached) {
                                            append("Arrives in ")
                                        } else {
                                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                                append("Arrived")
                                            }
                                        }
                                    }

                                    StopType.DEPARTURE -> {
                                        if (stopNotReached) {
                                            when (stop.travelMode) {
                                                "HEAVY_RAIL" -> {
                                                    append("Next train in ")
                                                }

                                                "BUS" -> {
                                                    append("Next bus in ")
                                                }
                                            }
                                        } else {
                                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                                append("Departed")
                                            }
                                        }
                                    }
                                }
                            }

                            if (stopNotReached) {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                ) {
                                    append("${stop.nextEventInMin} min")
                                }
                            }
                        }

                        Text(
                            text = annotatedString,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Route info section with visual action indicators
                    if (stop.routeName != null || stop.stopType == StopType.ARRIVAL) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(
                                        bottomStart = 12.dp,
                                        bottomEnd = 12.dp
                                    )
                                )
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(12.dp, 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left side: Action icons showing what to do
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                when (stop.stopType) {
                                    StopType.DEPARTURE -> {
                                        // Walking to stop
                                        Icon(
                                            painter = painterResource(R.drawable.ic_walk),
                                            contentDescription = "Walk",
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        HorizontalSpace(4)

                                        // Arrow indicating direction to transit
                                        Icon(
                                            painter = painterResource(R.drawable.double_arrow_right),
                                            contentDescription = "To",
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(28.dp)
                                        )

                                        HorizontalSpace(4)

                                        // Transit mode (bus/train)
                                        Icon(
                                            painter = painterResource(
                                                when (stop.travelMode) {
                                                    "HEAVY_RAIL" -> R.drawable.ic_train
                                                    "BUS" -> R.drawable.ic_bus
                                                    else -> R.drawable.ic_bus
                                                }
                                            ),
                                            contentDescription = "Transit",
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    StopType.ARRIVAL -> {
                                        // Transit mode (bus/train)
                                        Icon(
                                            painter = painterResource(R.drawable.outside_man),
                                            contentDescription = "Transit",
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        HorizontalSpace(4)

                                        // Arrow indicating exit from transit
                                        Icon(
                                            painter = painterResource(R.drawable.double_arrow_left),
                                            contentDescription = "From",
                                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                            modifier = Modifier.size(28.dp)
                                        )

                                        HorizontalSpace(4)

                                        // Destination/exit indicator (placeholder - can be customized)
                                        Icon(
                                            painter = painterResource(R.drawable.ic_walk),
                                            contentDescription = "Exit",
                                            tint = MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .graphicsLayer(scaleX = -1f) // Flip walking man to face left
                                        )
                                    }
                                }

                                HorizontalSpace(12)

                                // Vertical divider separating actions from route info
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(28.dp)
                                        .background(
                                            MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.3f
                                            )
                                        )
                                )
                            }

                            // Right side: Route name/number (only shown if its available)
                            if (stop.routeName != null) {
                                Text(
                                    text = stop.routeName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
            VerticalSpace(16)
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