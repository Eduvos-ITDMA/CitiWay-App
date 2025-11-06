package com.example.citiway.features.journey_summary

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.citiway.R
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.ui.components.HorizontalSpace
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.features.shared.Journey

@Composable
fun JourneySummaryContent(
    journey: Journey?,
    navController: NavController,
    paddingValues: PaddingValues
) {

    if (journey == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No journey selected for summary",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            VerticalSpace(16)
            Button(onClick = { navController.navigate(Screen.Home.route) }) {
                Text("Go back home")
            }
        }
    }

    // ========== DYNAMIC JOURNEY DATA CALCULATIONS ==========
    // These values are calculated from the journey data passed from JourneyViewModel
    // and formatted for display in the summary screen

    // Calculating start and end times
    val startTime = remember {
        // Start time: Current system time when user clicks "Start Journey"
        java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
    }

    val endTime = remember(journey?.arrivalTime) {
        // End time: Arrival time from journey data (calculated in ViewModel)
        journey?.arrivalTime?.atZone(java.time.ZoneId.systemDefault())?.toLocalTime()
            ?.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
            ?: "Unknown"
    }

    // Calculate date and duration
    val currentDate = remember {
        // Date: Current system date for demo purposes
        java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy"))
    }

    val duration = remember(journey) {
        // Duration: Sum of all instruction durations from journey
        journey?.instructions?.sumOf { it.durationMinutes } ?: 0
    }

    val formattedDuration = remember(duration) {
        // Format duration as "Xhr Ymins" or "Ymins" if less than 1 hour
        val hours = duration / 60
        val mins = duration % 60
        if (hours > 0) "${hours}hr ${mins}mins" else "${mins}mins"
    }

    val fareTotal = remember(journey?.fareTotal) {
        // Fare total: Total cost calculated in ViewModel, formatted as currency
        "R%.2f".format(journey?.fareTotal ?: 0.0)
    }
    // ========== END OF DYNAMIC JOURNEY DATA CALCULATIONS ==========


    // Track coordinates for progress line
    var stepCoordinates by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var boxOffset by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Travel Summary for\nYour Journey:",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            modifier = Modifier.fillMaxWidth()
        )

        VerticalSpace(24)

        // Start and End Time with Line
        JourneyTimelineHeader(
            startTime = startTime,
            endTime = endTime
        )

        VerticalSpace(24)

        // Journey Details Card
        JourneyDetailsCard(
            date = currentDate,
            duration = formattedDuration,
            fareTotal = fareTotal
        )

        VerticalSpace(32)

        // Journey Steps Timeline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    boxOffset = coordinates.positionInRoot()
                }
        ) {
            // Progress Line Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopStart)
            ) {
                // Draw connecting lines between steps with gaps around icons
                for (i in 0 until stepCoordinates.size - 1) {
                    val start = stepCoordinates[i]
                    val end = stepCoordinates[i + 1]

                    if (start != Offset.Zero && end != Offset.Zero) {
                        val adjustedStart = Offset(
                            x = start.x - boxOffset.x,
                            y = start.y - boxOffset.y
                        )
                        val adjustedEnd = Offset(
                            x = end.x - boxOffset.x,
                            y = end.y - boxOffset.y
                        )

                        // Calculate gap offset (spacing from icon center)
                        val gapSize = 24.dp.toPx()
                        val dx = adjustedEnd.x - adjustedStart.x
                        val dy = adjustedEnd.y - adjustedStart.y
                        val distance = kotlin.math.sqrt(dx * dx + dy * dy)

                        if (distance > gapSize * 2) {
                            // Normalize direction
                            val dirX = dx / distance
                            val dirY = dy / distance

                            // Apply gap from both ends
                            val lineStart = Offset(
                                x = adjustedStart.x + dirX * gapSize,
                                y = adjustedStart.y + dirY * gapSize
                            )
                            val lineEnd = Offset(
                                x = adjustedEnd.x - dirX * gapSize,
                                y = adjustedEnd.y - dirY * gapSize
                            )

                            // Dashed line between steps with gaps
                            drawLine(
                                color = Color(0xFFFFB74D),
                                start = lineStart,
                                end = lineEnd,
                                strokeWidth = 3.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                // Start Location
                SummaryLocationStep(
                    location = "52 Pienaar Rd, Milnerton, Cape Town, 7441",
                    subtitle = "Starting Location",
                    isStart = true,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            if (isEmpty()) add(offset) else set(0, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Walk 1
                SummaryWalkStep(
                    distanceMeters = 350,
                    durationMinutes = 6,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 2) add(Offset.Zero)
                            set(1, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Bus Stop 1
                SummaryTransitStep(
                    stopName = "Narwhal Bus Stop",
                    routeInfo = "Route T04",
                    isStation = false,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 3) add(Offset.Zero)
                            set(2, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Walk 2
                SummaryWalkStep(
                    distanceMeters = 200,
                    durationMinutes = 4,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 4) add(Offset.Zero)
                            set(3, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Train Stop 1
                SummaryTransitStep(
                    stopName = "Woodstock Station",
                    routeInfo = "Southern Line",
                    isStation = true,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 5) add(Offset.Zero)
                            set(4, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Bus Stop 2
                SummaryTransitStep(
                    stopName = "Observatory Bus Stop",
                    routeInfo = "Route T02",
                    isStation = false,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 6) add(Offset.Zero)
                            set(5, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Walk 3
                SummaryWalkStep(
                    distanceMeters = 180,
                    durationMinutes = 3,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 7) add(Offset.Zero)
                            set(6, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Train Stop 2
                SummaryTransitStep(
                    stopName = "Rondebosch Station",
                    routeInfo = "Northern Line",
                    isStation = true,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 8) add(Offset.Zero)
                            set(7, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Walk 4
                SummaryWalkStep(
                    distanceMeters = 420,
                    durationMinutes = 7,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 9) add(Offset.Zero)
                            set(8, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Bus Stop 3
                SummaryTransitStep(
                    stopName = "Mowbray Station",
                    routeInfo = "Route T06",
                    isStation = false,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 10) add(Offset.Zero)
                            set(9, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // Walk 5
                SummaryWalkStep(
                    distanceMeters = 150,
                    durationMinutes = 3,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 11) add(Offset.Zero)
                            set(10, offset)
                        }
                    }
                )

                VerticalSpace(32)

                // End Location
                SummaryLocationStep(
                    location = "Claremont Train Station",
                    subtitle = "Final Destination",
                    isStart = false,
                    onCoordinatesChanged = { offset ->
                        stepCoordinates = stepCoordinates.toMutableList().apply {
                            while (size < 12) add(Offset.Zero)
                            set(11, offset)
                        }
                    }
                )
            }
        }

        VerticalSpace(40)

        // Done Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                // TODO: Go home if just come from progress tracker, otherwise pop backstack
                onClick = { navController.navigate(Screen.Home.route) },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Done",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        VerticalSpace(32)
    }
}

@Composable
fun JourneyTimelineHeader(startTime: String, endTime: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Start Circle and Time
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape
                    )
            )
            VerticalSpace(8)
            Text(
                text = startTime,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Connecting Line
        Box(
            modifier = Modifier
                .weight(1f)
                .height(5.dp)
                .padding(horizontal = 16.dp)
                .background(Color(0xFFFFB74D))
        )

        // End Location Icon and Time
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_location_on),
                contentDescription = "Destination",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(32.dp)
            )
            VerticalSpace(8)
            Text(
                text = endTime,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun JourneyDetailsCard(date: String, duration: String, fareTotal: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            // Date
            DetailRow(label = "Date:", value = date)

            VerticalSpace(16)

            // Duration
            DetailRow(label = "Duration:", value = duration)

            VerticalSpace(16)

            // Fare Total
            DetailRow(label = "Fare Total:", value = fareTotal)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp
        )
    }
}

@Composable
fun SummaryLocationStep(
    location: String,
    subtitle: String,
    isStart: Boolean,
    onCoordinatesChanged: (Offset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        // Icon indicator - Larger
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .onGloballyPositioned { coordinates ->
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
                        .size(24.dp)
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
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        HorizontalSpace(12)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp
            )
            VerticalSpace(4)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SummaryWalkStep(
    distanceMeters: Int,
    durationMinutes: Int,
    onCoordinatesChanged: (Offset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Walk Icon - Larger
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .onGloballyPositioned { coordinates ->
                    val center = Offset(
                        x = coordinates.positionInRoot().x + coordinates.size.width / 2f,
                        y = coordinates.positionInRoot().y + coordinates.size.height / 2f
                    )
                    onCoordinatesChanged(center)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                contentDescription = "Walk",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(28.dp)
            )
        }

        HorizontalSpace(12)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Walk ${distanceMeters}m",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp
            )
            VerticalSpace(2)
            Text(
                text = "Approx. $durationMinutes min",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SummaryTransitStep(
    stopName: String,
    routeInfo: String,
    isStation: Boolean,
    onCoordinatesChanged: (Offset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Transit Icon - Larger
        Box(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .onGloballyPositioned { coordinates ->
                    val center = Offset(
                        x = coordinates.positionInRoot().x + coordinates.size.width / 2f,
                        y = coordinates.positionInRoot().y + coordinates.size.height / 2f
                    )
                    onCoordinatesChanged(center)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    if (isStation) R.drawable.ic_train else R.drawable.ic_bus
                ),
                contentDescription = if (isStation) "Train" else "Bus",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(28.dp)
            )
        }

        HorizontalSpace(12)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stopName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp
            )
            VerticalSpace(2)
            Text(
                text = routeInfo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}