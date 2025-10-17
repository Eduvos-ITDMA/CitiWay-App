package com.example.citiway.features.journey_selection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.citiway.R
import com.example.citiway.core.ui.components.HorizontalSpace
import com.example.citiway.core.ui.components.LocationSearchField
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.core.utils.JourneySelectionScreenPreview
import com.example.citiway.core.utils.toDisplayableLocalTime
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.features.shared.JourneyDetails
import com.example.citiway.features.shared.JourneySelectionActions
import com.example.citiway.features.shared.JourneyState
import com.example.citiway.features.shared.LocationType
import com.example.citiway.features.shared.TimeSlots
import com.example.citiway.features.shared.TimeType
import com.example.citiway.features.shared.TravelPoint
import java.time.Duration
import java.time.Instant

@Composable
fun JourneySelectionContent(
    state: JourneyState,
    actions: JourneySelectionScreenActions,
    startPlacesState: PlacesState,
    startPlacesActions: PlacesActions,
    destPlacesState: PlacesState,
    destPlacesActions: PlacesActions,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title("Select Your Journey")
        VerticalSpace(24)

        SelectedLocationFields(
            state,
            actions,
            startPlacesState,
            startPlacesActions,
            destPlacesState,
            destPlacesActions
        )
        VerticalSpace(8)

        JourneyOptionsSection(state, actions.journeySelectionActions)
        VerticalSpace(24)
    }
}

@Composable
fun SelectedLocationFields(
    state: JourneyState,
    actions: JourneySelectionScreenActions,
    startPlacesState: PlacesState,
    startPlacesActions: PlacesActions,
    destPlacesState: PlacesState,
    destPlacesActions: PlacesActions,
) {
    var startIconCoords by remember { mutableStateOf<Offset?>(null) }
    var endIconCoords by remember { mutableStateOf<Offset?>(null) }
    var canvasCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val connectorColor = MaterialTheme.colorScheme.secondary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    canvasCoords = coordinates
                }
                .zIndex(1f)
        ) {
            // Connector line drawing logic
            if (startIconCoords != null && endIconCoords != null && canvasCoords != null) {
                drawLine(
                    color = connectorColor,
                    start = startIconCoords!! - canvasCoords!!.positionInRoot(),
                    end = endIconCoords!! - canvasCoords!!.positionInRoot(),
                    strokeWidth = 4.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 30f)),
                )
            }
        }

        // Function to calculate co-ordinates for connector line
        fun calculateConnectorCoords(coordinates: LayoutCoordinates, locationType: LocationType) {
            val width = coordinates.size.width
            val height = coordinates.size.height

            val localCenter = Offset(
                x = width / 2f,
                y = if (locationType == LocationType.START) height.toFloat() else 0f
            )

            val coords = coordinates.localToRoot(localCenter)
            if (locationType == LocationType.START) {
                startIconCoords = coords
            } else {
                endIconCoords = coords
            }
        }

        // Section for both search fields with icons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(0.dp)
                .background(Color.Transparent)
        ) {
            LocationFieldWithIcon(
                actions.startLocationFieldActions,
                startPlacesState,
                startPlacesActions,
                state.startLocation?.primaryText ?: "",
            ) { modifier ->
                Icon(
                    painter = painterResource(R.drawable.ic_circle_full),
                    contentDescription = "Start location",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = modifier
                        .fillMaxSize(0.4f)
                        .onGloballyPositioned { coords ->
                            calculateConnectorCoords(coords, LocationType.START)
                        }
                )
            }

            LocationFieldWithIcon(
                actions.destinationFieldActions,
                destPlacesState,
                destPlacesActions,
                state.destination?.primaryText ?: "",
            ) { modifier ->
                Icon(
                    painter = painterResource(R.drawable.ic_location_on),
                    contentDescription = "End location",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = modifier
                        .fillMaxSize(0.5f)
                        .onGloballyPositioned { coords ->
                            calculateConnectorCoords(coords, LocationType.END)
                        }
                )
            }
        }
    }
}

@Composable
fun LocationFieldWithIcon(
    locationFieldActions: LocationFieldActions,
    placesState: PlacesState,
    placesActions: PlacesActions,
    initialValue: String = "",
    connectorIcon: @Composable (Modifier) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(0.dp, 8.dp)
            .height(IntrinsicSize.Min)
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocationSearchField(
            modifier = Modifier.weight(1f),
            icon = { modifier ->
                Icon(
                    painterResource(R.drawable.ic_map),
                    contentDescription = "Open Map",
                    modifier = modifier.clickable { locationFieldActions.onFieldIconClick() }
                )
            },
            onSelectPrediction = locationFieldActions.onSelectPrediction,
            placesState = placesState,
            placesActions = placesActions,
            initialValue = initialValue
        )
        Box(
            modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()
                .padding(0.dp)
        ) {
            connectorIcon(Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun JourneyOptionsSection(state: JourneyState, actions: JourneySelectionActions) {
    Column {
        TimeSlotSelector(state, actions)
        VerticalSpace(12)

        val mockJourneyDetails = listOf(
            // Scenario 1: Simple Bus route
            JourneyDetails(
                firstWalkMinutes = 5,
                firstNodeType = TravelPoint.STOP,
                routeSegments = listOf("Walk", "MyCiTi"),
                nextDeparture = Duration.ofMinutes(12),
                arrivalTime = Instant.now().plus(Duration.ofMinutes(45)),
                fareTotal = 25.50f
            ),

            // Scenario 2: Train route with a transfer to a bus
            JourneyDetails(
                firstWalkMinutes = 8,
                firstNodeType = TravelPoint.STATION,
                routeSegments = listOf("Walk", "Metrorail", "Walk", "MyCiTi"),
                nextDeparture = Duration.ofMinutes(3),
                arrivalTime = Instant.now().plus(Duration.ofMinutes(75)),
                fareTotal = 42.00f
            ),

            // Scenario 3: Longer walk, multiple bus segments
            JourneyDetails(
                firstWalkMinutes = 15,
                firstNodeType = TravelPoint.STOP,
                routeSegments = listOf("Walk", "MyCiTi", "Walk", "MyCiTi"),
                nextDeparture = Duration.ofMinutes(25),
                arrivalTime = Instant.now().plus(Duration.ofMinutes(60)),
                fareTotal = 30.00f
            ),
        )

        if (state.journeyOptions == null) {
            NoJourneyOptionsAvailable()
        } else if (state.journeyOptions.isEmpty()) {
            JourneyLoadingIndicator()
        } else {
            state.journeyOptions.forEach { journey ->
                JourneyCard(journey)
                VerticalSpace(24)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotSelector(state: JourneyState, actions: JourneySelectionActions) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            // Dropdown for Departure/Arrival
            TimeSlotDropDown(
                TimeType.entries,
                state.filter.timeType.name,
                { timeType -> timeType.name })
            { timeType: String ->
                actions.onSetTimeType(TimeType.valueOf(timeType))
                actions.onGetJourneyOptions()
            }

            // Text in the middle
            Text(
                text = " at about ",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            // Dropdown for Time Slots
            TimeSlotDropDown(
                TimeSlots,
                state.selectedTimeString,
                { hourString -> hourString })
            { time: String ->
                actions.onSetTime(time)
                actions.onGetJourneyOptions()
            }
        }
    }
}

/**
 * Main Composable function for the entire Trip Summary UI card.
 */
@Composable
fun JourneyCard(journey: JourneyDetails) {
    val cornerRadius = 12.dp

    Card(
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TripHeader(cornerRadius, journey.arrivalTime, journey.fareTotal)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                JourneyDetailRow(
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    content = buildAnnotatedString {
                        val walkDuration = journey.firstWalkMinutes
                        if (walkDuration != null) {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(journey.firstWalkMinutes.toString() + "m")
                            }
                            append(" walk to first station")
                        } else {
                            append("Unknown walk time to first station")
                        }
                    }
                )
                VerticalSpace(12)

                RouteDescriptionRow(journey.routeSegments)
                VerticalSpace(12)

                JourneyDetailRow(
                    icon = Icons.Default.HourglassEmpty,
                    content = buildAnnotatedString {
                        val min = journey.nextDeparture?.toMinutes()?.toInt()
                        if (min != null) {
                            append("Next departure in ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("$min min")
                            }
                        } else {
                            append("Next departure time unknown")
                        }
                    }
                )
            }

            StartJourneyButton()
        }
    }
}

/**
 * Composable for the blue header of the card.
 */
@Composable
fun TripHeader(cornerRadius: Dp, arrivalTime: Instant?, fareTotal: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Clock Icon
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = "Arrival Time",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier.size(24.dp)
        )
        HorizontalSpace(8)

        // Arrival Time Text
        Text(
            text = "Arrival Time: ${arrivalTime?.toDisplayableLocalTime() ?: "N/A"}",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        // Price Text
        Text(
            text = "R$fareTotal",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

/**
 * A reusable composable for a detail row with an icon and styled text.
 */
@Composable
fun JourneyDetailRow(icon: ImageVector, content: AnnotatedString) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Transport mode order",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )
        HorizontalSpace(10)
        Text(
            text = content,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Composable for the complex, multi-line route description.
 */
@Composable
fun RouteDescriptionRow(routeSegments: List<String>?) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Custom Bullet Point (Solid Circle)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(24.dp)
                .offset(y = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        MaterialTheme.colorScheme.onBackground,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .align(Alignment.TopCenter)
                    .offset(y = 6.dp)
            )
        }
        HorizontalSpace(14)

        // Styled Route Text
        Text(
            text = createStyledRouteString(routeSegments),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Composable for the interactive "Start Journey" area at the bottom.
 */
@Composable
fun StartJourneyButton() {
    // This uses the bottom rounded corners of the main card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) // Light blue background
            .clickable { /* TODO: Handle journey start click */ }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Start Journey",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun createStyledRouteString(
    routeSegments: List<String>?,
): AnnotatedString {
    return buildAnnotatedString {
        routeSegments?.forEachIndexed { index, segment ->
            // Assume any segment that is NOT "Walk" is a transit mode/station name
            val style = if (segment.trim().uppercase() != "WALK") {
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                SpanStyle(color = MaterialTheme.colorScheme.onBackground)
            }

            withStyle(style = style) {
                append(segment)
            }

            // Append separator " -- " if not the last item
            if (index < routeSegments.lastIndex) {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                    append(" -- ")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> TimeSlotDropDown(
    entries: List<T>,
    defaultText: String,
    getEntryText: (T) -> String,
    onSelect: (String) -> Unit
) {
    // State for the TimeType dropdown (Departure/Arrival)
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(defaultText) }

    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                RoundedCornerShape(8.dp)
            )
            .padding(0.dp, 0.dp, 4.dp, 0.dp)
            .clickable { expanded = !expanded }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = text,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .padding(8.dp),
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    expanded = !expanded
                                }
                            }
                        }
                    },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                ),
            )
            Box(Modifier.fillMaxHeight()) {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentWidth()
        ) {
            entries.forEach { entry ->
                DropdownMenuItem(
                    text = {
                        Text(
                            getEntryText(entry),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    onClick = {
                        text = getEntryText(entry)
                        onSelect(text)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(5.dp)
                    )
                )
            }
        }
    }
}

@Composable
fun JourneyLoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        VerticalSpace(16)
        Text(
            text = "Finding the best routes for you...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun NoJourneyOptionsAvailable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "No routes found",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
        VerticalSpace(16)
        Text(
            text = "No transit options were found for the selected locations and time.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Preview
@Composable
fun JourneySelectionPreview() {
    JourneySelectionScreenPreview()
}
