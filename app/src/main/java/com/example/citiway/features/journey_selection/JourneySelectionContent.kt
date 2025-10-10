package com.example.citiway.features.journey_selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.citiway.R
import com.example.citiway.core.ui.components.LocationSearchField
import com.example.citiway.core.ui.components.Title
import com.example.citiway.core.ui.components.VerticalSpace
import com.example.citiway.core.utils.JourneySelectionScreenPreview
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.features.shared.JourneyState

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
            .padding(horizontal = 16.dp),
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {

        LocationFieldWithIcon(
            state,
            actions.startLocationFieldActions,
            placesState,
            placesActions
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_location_on),
                contentDescription = "End location",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    LocationFieldWithIcon(state, actions.startLocationFieldActions, placesState, placesActions) {
        Icon(
            painter = painterResource(R.drawable.ic_location_on),
            contentDescription = "End location",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LocationFieldWithIcon(
    journeyState: JourneyState,
    locationFieldActions: LocationFieldActions,
    placesState: PlacesState,
    placesActions: PlacesActions,
    connectorIcon: @Composable (Modifier) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(0.dp, 8.dp)
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
            initialValue = journeyState.startLocation?.primaryText ?: ""
        )
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .padding(0.dp)
        ) {
            connectorIcon(Modifier.fillMaxSize(1f))
        }
    }
}

@Preview
@Composable
fun JourneySelectionPreview() {
    JourneySelectionScreenPreview()
}
