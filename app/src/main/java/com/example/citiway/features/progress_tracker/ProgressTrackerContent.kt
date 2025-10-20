package com.example.citiway.features.journey_progress

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
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.citiway.R
import com.example.citiway.core.ui.components.HorizontalSpace
import com.example.citiway.core.ui.components.VerticalSpace

@Composable
fun ProgressTrackerContent(navController: NavController, paddingValues: PaddingValues) {

    // Track coordinates for progress line
    var stepCoordinates by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var boxOffset by remember { mutableStateOf(Offset.Zero) } //  Tracks the Box position, stops the bug when i scroll

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

        // ETA and Distance Card will take data from the selection screen. To be injected with data from API
        ETACard(eta = "11:34 am", distance = "27km")

        VerticalSpace(24)

        // Journey Steps with Progress Line ####################################################################
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // Tracking the Box's position
                    boxOffset = coordinates.positionInRoot()
                }
        )
        {
            // Progress Line Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize() // This makes the canvas cover the entire Box
                    .align(Alignment.TopStart) // CentreStart broke it,  goes to first circle.
            ) {
                // Draw connecting lines between steps
                for (i in 0 until stepCoordinates.size - 1) {
                    val start = stepCoordinates[i]
                    val end = stepCoordinates[i + 1]

                    // Only draw if coordinates are valid
                    if (start != Offset.Zero && end != Offset.Zero) {
                        // Now adjustss coordinates relative to the Box
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
                            color = Color(0xFFFFB74D), // Making the line Orange
                            start = adjustedStart,
                            end = adjustedEnd,
                            strokeWidth = 4.dp.toPx(),  // Line thickness
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f))  // First number = dash length, second = gap length
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                // Start Location
                JourneyStep(
                    isStart = true, // Cirle when true, location pin when false. For end destination.
                    title = "52 Pienaar Rd, Milnerton", // will be injected from api object/viewmodel
                    hasEditIcon = true,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            if (isEmpty()) add(offset) else set(0, offset)
                        }
                    }
                )

                VerticalSpace(8)

                // Walk step
                WalkStep(
                    duration = "400m",
                    time = "approx. 5min"
                )

                VerticalSpace(8)

                // Narwhal Bus Stop
                TransitStopCard(
                    stopName = "Narwhal Bus Stop",
                    nextTransit = "Next bus in 11min",
                    routeName = "Route T04",
                    transitDetail = "Take MyCiTi bus for 6 stops",
                    transitTime = "approx. 9 min",
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 2) add(Offset.Zero)
                            set(1, offset)
                        }
                    }
                )

                VerticalSpace(8)

                // Woodstock Station
                TransitStopCard(
                    stopName = "Woodstock Station",
                    nextTransit = "Next train in 22min",
                    routeName = "Southern Line",
                    transitDetail = "Take train for 3 stations",
                    transitTime = "approx. 12 min",
                    isStation = true,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 3) add(Offset.Zero)
                            set(2, offset)
                        }
                    }
                )

                VerticalSpace(8)

                // Mowbray Station
                TransitStopCard(
                    stopName = "Mowbray Station",
                    nextTransit = "Next train in 22min",
                    routeName = "Disembark",
                    routeColor = Color(0xFFB0BEC5),
                    transitDetail = null,
                    isStation = true,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 4) add(Offset.Zero)
                            set(3, offset)
                        }
                    }
                )

                VerticalSpace(8)

                // Final walk
                WalkStep(
                    duration = "250m",
                    time = "approx. 4 min"
                )

                VerticalSpace(8)

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
                onClick = { /* TODO: Handle cancel */ }, // Ask caleb cancel to where. Back to home or dest would be best.
                modifier = Modifier
                    .fillMaxWidth(0.6f) // Using 60% of available width for better proportions
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Cancel Journey",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        VerticalSpace(24)
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

            Divider(
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
                            MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_location_on),
                    contentDescription = "Destination",
                    tint = MaterialTheme.colorScheme.primary,
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
            border = CardDefaults.outlinedCardBorder(),
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
fun WalkStep(duration: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Empty space for progress line ....
        Box(modifier = Modifier.width(40.dp).fillMaxHeight())

        HorizontalSpace(12) // gap for alignment leave space for the tracker

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                contentDescription = "Walk",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )

            HorizontalSpace(8)

            Column {
                Text(
                    text = "Walk $duration", // would inject data here from api object/viewmodel
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun TransitStopCard( // ui done. card with details. *Ask caleb for commponent
    stopName: String,
    nextTransit: String,
    routeName: String,
    routeColor: Color = MaterialTheme.colorScheme.secondary,
    transitDetail: String?,
    transitTime: String? = null,
    isStation: Boolean = false,
    onCoordinatesChanged: (Offset) -> Unit
) {
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
                            text = stopName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        VerticalSpace(2)

                        // "Next bus in 11min" with orange color for time
                        val annotatedString = buildAnnotatedString {
                            val parts = nextTransit.split(" ")
                            parts.forEachIndexed { index, part ->
                                if (part.contains("min", ignoreCase = true) ||
                                    part.any { it.isDigit() }) {
                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(part)
                                    }
                                } else {
                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                        )
                                    ) {
                                        append(part)
                                    }
                                }
                                if (index < parts.lastIndex) append(" ")
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
                                imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                                contentDescription = "Walk",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )

                            HorizontalSpace(8)

                            Icon(
                                painter = painterResource(R.drawable.double_arrow_right), // Downloaded icon from site fonts.google, can be customised.
                                contentDescription = "To",
                                tint = routeColor,
                                modifier = Modifier.size(28.dp)
                            )

                            HorizontalSpace(8)

                            Icon(
                                painter = painterResource(
                                    if (isStation) R.drawable.ic_train else R.drawable.ic_bus
                                ),
                                contentDescription = if (isStation) "Train" else "Bus",
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

            // Transit details below card
            if (transitDetail != null) {
                VerticalSpace(8)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            if (isStation) R.drawable.ic_train else R.drawable.ic_bus
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(20.dp)
                    )

                    HorizontalSpace(8)

                    Column {
                        Text(
                            text = transitDetail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (transitTime != null) {
                            Text(
                                text = transitTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}