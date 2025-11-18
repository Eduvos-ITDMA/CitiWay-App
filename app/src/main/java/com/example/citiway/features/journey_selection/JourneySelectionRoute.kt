package com.example.citiway.features.journey_selection

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.core.utils.getNearestHalfHour
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.JourneySelectionActions
import com.example.citiway.features.shared.JourneyViewModel
import com.example.citiway.features.shared.LocationType
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun JourneySelectionRoute(
    navController: NavController,
) {
    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        })
    journeyViewModel.setTime(getNearestHalfHour())

    val journeyState by journeyViewModel.state.collectAsStateWithLifecycle()

    // Addition places state and actions for second location search field component
    // This is necessary as each component must have its own state so that changes to the one does
    // not affect the other
    val startPlacesManager = remember { App.appModule.placesManagerFactory.create() }
    val startPlacesState by startPlacesManager.state.collectAsStateWithLifecycle()
    val startPlacesActions = startPlacesManager.actions

    val destPlacesManager = remember { App.appModule.placesManagerFactory.create() }
    val destPlacesState by destPlacesManager.state.collectAsStateWithLifecycle()
    val destPlacesActions = destPlacesManager.actions

    // Created a stable key that only changes when both locations are valid
    // only including the coordinates, not the entire location object
    val locationsKey = remember(
        journeyState.startLocation?.latLng?.latitude,
        journeyState.startLocation?.latLng?.longitude,
        journeyState.destination?.latLng?.latitude,
        journeyState.destination?.latLng?.longitude
    ) {
        val startLat = journeyState.startLocation?.latLng?.latitude
        val startLng = journeyState.startLocation?.latLng?.longitude
        val destLat = journeyState.destination?.latLng?.latitude
        val destLng = journeyState.destination?.latLng?.longitude

        "${startLat}_${startLng}_${destLat}_${destLng}"
    }

    LaunchedEffect(locationsKey) {  // Now Only triggers when coordinates change
        val startLocation = journeyState.startLocation
        val destination = journeyState.destination

        if (startLocation != null && destination != null) {
            // Update search text fields
            startPlacesActions.onSetSearchText(startLocation.primaryText)
            destPlacesActions.onSetSearchText(destination.primaryText)

            // Only fetch journey options if we have valid coordinates
            if (startLocation.latLng.latitude != 0.0 &&
                startLocation.latLng.longitude != 0.0 &&
                destination.latLng.latitude != 0.0 &&
                destination.latLng.longitude != 0.0) {

                Log.d("JourneySelection", "Fetching routes for: ${startLocation.primaryText} → ${destination.primaryText}")
                journeyViewModel.getJourneyOptions()
            }
        }
    }

    // Clean up Coroutine Scopes in PlacesManager instances
    DisposableEffect(Unit) {
        onDispose {
            startPlacesManager.cancel()
            destPlacesManager.cancel()
        }
    }

    val onSelectionPrediction: (LocationType, AutocompletePrediction) -> Unit =
        { locationType, prediction ->
            val placesActions = when (locationType) {
                LocationType.START -> startPlacesActions
                LocationType.END -> destPlacesActions
            }

            journeyViewModel.viewModelScope.launch {
                val selectedLocation = placesActions.getPlace(prediction)
                if (selectedLocation != null) {
                    placesActions.onSetSearchText(selectedLocation.primaryText)
                    when (locationType) {
                        LocationType.START -> journeyViewModel.actions.onSetStartLocation(
                            selectedLocation
                        )

                        LocationType.END -> journeyViewModel.actions.onSetDestination(
                            selectedLocation
                        )
                    }


                    // Note: getJourneyOptions() will be called automatically by LaunchedEffect
                    // when the state updates, so we don't need to call it here
                }
            }
        }

    val journeySelectionActions = JourneySelectionScreenActions(
        journeyViewModel.actions,
        { id ->
            journeyViewModel.actions.onSetJourney(id)
            navController.navigate(Screen.ProgressTracker.route)
        },
        LocationFieldActions(onFieldIconClick = {
            val startLocation = journeyState.startLocation
            if (startLocation != null) {
                startPlacesActions.onSetSearchText(startLocation.primaryText)
                startPlacesActions.onSetSelectedLocation(startLocation)
                navController.navigate(Screen.StartLocationSelection.route)
            }
        }, onSelectPrediction = { prediction ->
            onSelectionPrediction(
                LocationType.START, prediction
            )
        }),
        LocationFieldActions(onFieldIconClick = {
            val destination = journeyState.destination
            if (destination != null) {
                destPlacesActions.onSetSearchText(destination.primaryText)
                destPlacesActions.onSetSelectedLocation(destination)
                navController.navigate(Screen.DestinationSelection.route)
            }
        }, onSelectPrediction = { prediction ->
            onSelectionPrediction(
                LocationType.END, prediction
            )
        }),

        )

    ScreenWrapper(navController, true, { paddingValues ->
        JourneySelectionContent(
            journeyState,
            journeySelectionActions,
            startPlacesState,
            startPlacesActions,
            destPlacesState,
            destPlacesActions,
            paddingValues,
        )
    })
}

data class JourneySelectionScreenActions(
    val journeySelectionActions: JourneySelectionActions,
    val onConfirmJourneySelection: (journeyId: String) -> Unit,
    val startLocationFieldActions: LocationFieldActions,
    val destinationFieldActions: LocationFieldActions,
)

data class LocationFieldActions(
    val onFieldIconClick: () -> Unit,
    val onSelectPrediction: (AutocompletePrediction) -> Unit,
)
