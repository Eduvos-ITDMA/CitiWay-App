package com.example.citiway.features.journey_selection

import android.annotation.SuppressLint
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
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.JourneySelectionActions
import com.example.citiway.features.shared.JourneyViewModel
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
        }
    )
    journeyViewModel.setTime(getNearestHalfHour())
    journeyViewModel.getJourneyOptions()

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

    // Initialize search text
    LaunchedEffect(Unit) {
        startPlacesActions.onSetSearchText(
            journeyState.startLocation?.primaryText ?: "error"
        )
        destPlacesActions.onSetSearchText(
            journeyState.destination?.primaryText ?: "error"
        )
    }

    // Clean up Coroutine Scopes in PlacesManager instances
    DisposableEffect(Unit) {
        onDispose {
            startPlacesManager.cancel()
            destPlacesManager.cancel()
        }
    }

    val journeySelectionActions = JourneySelectionScreenActions(
        journeyViewModel.actions,
        { id ->
            journeyViewModel.actions.onSetJourney(id)
            navController.navigate(Screen.ProgressTracker.route)
        },
        LocationFieldActions(
            onFieldIconClick = {
                val startLocation = journeyState.startLocation
                if (startLocation != null) {
                    startPlacesActions.onSetSearchText(startLocation.primaryText)
                    startPlacesActions.onSetSelectedLocation(startLocation)
                    navController.navigate(Screen.StartLocationSelection.route)
                }
            },
            onSelectPrediction = { prediction ->
                journeyViewModel.viewModelScope.launch {
                    val selectedLocation = startPlacesActions.getPlace(prediction)
                    if (selectedLocation != null) {
                        journeyViewModel.actions.onSetStartLocation(selectedLocation)
                    }
                }
            }
        ),
        LocationFieldActions(
            onFieldIconClick = {
                val destination = journeyState.startLocation
                if (destination != null) {
                    destPlacesActions.onSetSearchText(destination.primaryText)
                    destPlacesActions.onSetSelectedLocation(destination)
                    navController.navigate(Screen.DestinationSelection.route)
                }
            },
            onSelectPrediction = { prediction ->
                journeyViewModel.viewModelScope.launch {
                    val selectedLocation = destPlacesActions.getPlace(prediction)
                    if (selectedLocation != null) {
                        journeyViewModel.actions.onSetDestination(selectedLocation)
                    }
                }
            }
        ),

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
