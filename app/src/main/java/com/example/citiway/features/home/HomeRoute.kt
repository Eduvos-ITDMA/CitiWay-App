package com.example.citiway.features.home

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel
import com.example.citiway.features.shared.JourneyViewModel
import com.example.citiway.features.shared.LocationType
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.launch

data class HomeActions(
    val onToggleFavourite: (String) -> Unit,
    val onSchedulesLinkClick: () -> Unit,
    val onMapIconClick: () -> Unit,
    val onSelectPrediction: (AutocompletePrediction) -> Unit,
    val onFavouritesTitleClick: () -> Unit,
    val onRecentTitleClick: () -> Unit,
)

@Composable
fun HomeRoute(
    navController: NavController,
    completedJourneysViewModel: CompletedJourneysViewModel = viewModel()
) {
    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        })

    val placesManager = App.appModule.placesManager
    val placesState by placesManager.state.collectAsStateWithLifecycle()
    val placesActions = placesManager.actions

    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()

    val onSelectPrediction: (AutocompletePrediction) -> Unit =
        { prediction ->
            // Set selectedLocation using prediction
            journeyViewModel.viewModelScope.launch {
                val selectedLocation = placesActions.getPlace(prediction)
                journeyViewModel.confirmLocationSelection(
                    selectedLocation, LocationType.END, placesActions.onClearSearch
                )
            }
        }

    val actions = HomeActions(
        completedJourneysViewModel::toggleFavourite,
        { navController.navigate(Screen.Schedules.route) },
        { navController.navigate(Screen.DestinationSelection.route) },
        onSelectPrediction,
        onFavouritesTitleClick = { navController.navigate(Screen.Favourites.route) },
        onRecentTitleClick = { navController.navigate(Screen.JourneyHistory.route) }
    )

    ScreenWrapper(navController, true, { paddingValues ->
        HomeContent(
            completedJourneysState = completedJourneysState,
            homeActions = actions,
            placesState = placesState,
            placesActions = placesActions,
            paddingValues = paddingValues,
        )
    })
}
