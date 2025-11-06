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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.citiway.R
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.ui.components.HorizontalSpace
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.core.utils.JourneySummaryScreenPreview
import com.example.citiway.core.utils.formatMinutesToHoursAndMinutes
import com.example.citiway.core.utils.toDisplayableLocalTime
import com.example.citiway.core.utils.toLocalDateTime
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.features.shared.Instruction
import com.example.citiway.features.shared.Journey
import java.time.Duration
import java.time.format.DateTimeFormatter

@Composable
fun JourneySummaryContent(
    journey: Journey?,
    startLocation: SelectedLocation?,
    destination: SelectedLocation?,
    navController: NavController,
    paddingValues: PaddingValues
) {
    if (journey == null || startLocation == null || destination == null) {
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
        return
    }

    // ========== Journey Data Extraction ==========
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val startTime = journey.startTime.toDisplayableLocalTime()
    val arrivalTime = journey.arrivalTime?.toDisplayableLocalTime() ?: startTime
    val journeyDate = journey.startTime.toLocalDateTime().toLocalDate().format(formatter)
    val journeyDuration =
        Duration.between(journey.startTime, journey.arrivalTime ?: journey.startTime).toMinutes()
            .toInt()
    val fareTotal = journey.fareTotal

    // Track coordinates for progress line
    val stepsCount = journey.stops.size.times(2).plus(2)
    var stepCoordinates by remember {
        mutableStateOf(Array<Offset?>(stepsCount) { null })
    }
    var boxOffset by remember { mutableStateOf(Offset.Zero) }
    val connectorColour = MaterialTheme.colorScheme.secondary

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
            arrivalTime = arrivalTime
        )

        VerticalSpace(24)

        // Journey Details Card
        JourneyDetailsCard(
            date = journeyDate,
            duration = journeyDuration,
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
                val yOffset = 20.dp.toPx()

                // Draw connecting lines between steps
                for (i in stepCoordinates.dropLast(1).indices) {
                    val current = stepCoordinates[i]
                    val next = stepCoordinates[i + 1]

                    if (current != Offset.Zero && current != null && next != Offset.Zero && next != null) {

                        val startPoint = Offset(current.x, current.y + yOffset)
                        val endPoint = Offset(next.x, next.y - yOffset)

                        drawLine(
                            color = connectorColour,
                            start = startPoint,
                            end = endPoint,
                            strokeWidth = 4.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                // Start Location
                SummaryLocationStep(
                    name = startLocation.primaryText,
                    isStart = true
                ) { offset ->
                    updateCoordinate(0, offset)
                }

                VerticalSpace(40)

                // First Instruction
                SummaryInstructionStep(journey.instructions.first()) { offset ->
                    updateCoordinate(1, offset)
                }

                // =========================================
                // Create step for each stop and instruction
                // =========================================
                journey.stops.forEachIndexed { index, stop ->
                    val coordsIndex = (index + 1) * 2

                    VerticalSpace(40)
                    SummaryTransitStep(stop.name, stop.routeName, stop.travelMode) { offset ->
                        updateCoordinate(coordsIndex, offset)
                    }

                    VerticalSpace(40)
                    val instruction = journey.instructions[index + 1]
                    SummaryInstructionStep(instruction) { offset ->
                        updateCoordinate(coordsIndex + 1, offset)
                    }
                }

                VerticalSpace(40)
                // End Location
                SummaryLocationStep(
                    name = destination.primaryText,
                    isStart = false
                ) { offset ->
                    updateCoordinate(stepsCount - 1, offset)
                }
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
fun JourneyTimelineHeader(startTime: String, arrivalTime: String) {
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
                text = arrivalTime,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun JourneyDetailsCard(date: String, duration: Int, fareTotal: Double) {
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
            DetailRow(label = "Duration:", value = formatMinutesToHoursAndMinutes(duration))

            VerticalSpace(16)

            // Fare Total
            DetailRow(label = "Fare Total:", value = "R$fareTotal")
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
    name: String,
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
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
fun SummaryInstructionStep(
    instruction: Instruction,
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
                text = instruction.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp
            )
            VerticalSpace(2)
            Text(
                text = "Approx. ${instruction.durationMinutes} min",
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
    routeName: String?,
    travelMode: String?,
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
                painter = painterResource(modeIcon(travelMode)),
                contentDescription = travelMode ?: "Unknown travel mode",
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
                text = routeName ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
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
fun JourneySummaryPrevious() {
    JourneySummaryScreenPreview()
}

//// WIP: Waiting on inject, will be called when journey is done for VW code to run and save to DB. Ask caleb
//fun onJourneyComplete() {
//    val userId = getCurrentUserId()  // Getting  user from db/auth will be 1
//
//    viewModelScope.launch {
//        saveCompletedJourney(userId)
//
//        // Then navigate to home
//        navController.navigate(Screen.Home.route)
//    }
//}
