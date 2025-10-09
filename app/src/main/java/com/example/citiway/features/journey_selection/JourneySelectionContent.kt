package com.example.citiway.features.journey_selection

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.citiway.R
import com.example.citiway.core.ui.components.LocationSearchField
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.core.utils.JourneySelectionScreenPreview
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.features.shared.JourneyState
import com.example.citiway.features.shared.LocationType

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
    }
}

@Composable
fun SelectedLocationFields(
    state: JourneyState,
    actions: JourneySelectionActions,
    placesState: PlacesState,
    placesActions: PlacesActions
) {
    Log.d("Journey State", state.toString())
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

@Preview
@Composable
fun JourneySelectionPreview() {
    JourneySelectionScreenPreview()
}
