package com.example.citiway.features.journey_selection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.citiway.R
import com.example.citiway.core.ui.components.HorizontalSpace
import com.example.citiway.core.ui.components.LocationSearchField
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.core.utils.JourneySelectionScreenPreview
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.features.shared.JourneyDetails
import com.example.citiway.features.shared.JourneyState
import com.example.citiway.features.shared.LocationType
import com.example.citiway.features.shared.TravelPoint
import java.time.LocalTime

@Composable
fun JourneySelectionContent(
    state: JourneyState,
    actions: JourneySelectionActions,
    placesState: PlacesState,
    placesActions: PlacesActions,
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

        SelectedLocationFields(state, actions, placesState, placesActions)
        VerticalSpace(24)

        JourneyOptionsSection(state)
    }
}

@Composable
fun SelectedLocationFields(
    state: JourneyState,
    actions: JourneySelectionActions,
    placesState: PlacesState,
    placesActions: PlacesActions
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
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f)),
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
                placesState,
                placesActions,
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
                placesState,
                placesActions,
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
fun JourneyOptionsSection(state: JourneyState) {
    Column {
        state.journeyOptions = listOf(
            JourneyDetails(
                10,
                TravelPoint.STOP,
                listOf("Walk", "MyCiTi", "Walk", "Metrorail", "Walk"),
                15,
                LocalTime.of(8, 30)
            ), JourneyDetails(
                8,
                TravelPoint.STATION,
                listOf("Walk", "Metrorail", "Walk", "MyCiTi", "Walk"),
                20,
                LocalTime.of(15, 45)
            )
        )
        state.journeyOptions.forEach { journey ->
            JourneyCard(journey)
            VerticalSpace(24)
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
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("8 min")
                        }
                        append(" walk to first station")
                    }
                )
                VerticalSpace(12)

                RouteDescriptionRow(journey.routeSegments)
                VerticalSpace(12)

                JourneyDetailRow(
                    icon = Icons.Default.HourglassEmpty,
                    content = buildAnnotatedString {
                        append("Next Departure in ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("20mins")
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
fun TripHeader(cornerRadius: Dp, arrivalTime: LocalTime, fareTotal: Float) {
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
            text = "Arrival Time: $arrivalTime",
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
fun RouteDescriptionRow(routeSegments: List<String>) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
    ) {
        // Custom Bullet Point (Solid Circle)
        Box(
            modifier = Modifier
                .fillMaxHeight()
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
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun createStyledRouteString(
    routeSegments: List<String>,
): AnnotatedString {
    return buildAnnotatedString {
        routeSegments.forEachIndexed { index, segment ->
            // Assume any segment that is NOT "Walk" is a transit mode/station name
            val isTransit = segment.trim().uppercase() != "WALK"
            val style = if (isTransit) {
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

@Preview
@Composable
fun JourneySelectionPreview() {
    JourneySelectionScreenPreview()
}
