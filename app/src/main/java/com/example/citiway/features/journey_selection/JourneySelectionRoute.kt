package com.example.citiway.features.journey_selection

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.data.remote.PlacesManager
import com.example.citiway.data.remote.PlacesState
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.JourneyViewModel
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.launch

@Composable
fun JourneySelectionRoute(
    navController: NavController,
) {
    val placesManager = App.appModule.placesManager
    val placesState by placesManager.state.collectAsStateWithLifecycle()
    val placesActions = placesManager.actions

    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        }
    )
    journeyViewModel.setJourneyOptions()

    val journeyState by journeyViewModel.state.collectAsStateWithLifecycle()
    val journeySelectionActions = JourneySelectionActions(
        LocationFieldActions(
            onFieldIconClick = {
                val startLocation = journeyState.startLocation
                if (startLocation != null){
                    placesActions.onSetSearchText(startLocation.primaryText)
                    placesActions.onSetSelectedLocation(startLocation)
                }
            },
            onSelectPrediction = { prediction ->
                journeyViewModel.viewModelScope.launch {
                    val selectedLocation = placesActions.getPlace(prediction)
                    journeyState.startLocation = selectedLocation
                }
            }
        ),
        LocationFieldActions(
            onFieldIconClick = {
                val destination = journeyState.startLocation
                if (destination != null){
                    placesActions.onSetSearchText(destination.primaryText)
                    placesActions.onSetSelectedLocation(destination)
                }
            },
            onSelectPrediction = { prediction ->
                journeyViewModel.viewModelScope.launch {
                    val selectedLocation = placesActions.getPlace(prediction)
                    journeyState.destination = selectedLocation
                }
            }
        )
    )

    ScreenWrapper(navController, true, { paddingValues ->
        JourneySelectionContent(journeyState, journeySelectionActions, placesState, placesActions, paddingValues)
    })
}

data class JourneySelectionActions(
    val startLocationFieldActions: LocationFieldActions,
    val destinationFieldActions: LocationFieldActions
)

data class LocationFieldActions(
    val onFieldIconClick: () -> Unit,
    val onSelectPrediction: (AutocompletePrediction) -> Unit,
)
