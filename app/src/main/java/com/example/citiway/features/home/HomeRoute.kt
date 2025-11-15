package com.example.citiway.features.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.data.local.entities.Trip
import com.example.citiway.data.remote.SelectedLocation
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel
import com.example.citiway.features.shared.JourneyViewModel
import com.example.citiway.features.shared.LocationType
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Action callbacks for Home screen interactions
 * Centralizes all user actions for cleaner code organization
 */
data class HomeActions(
    val onToggleFavourite: (String) -> Unit,
    val onSchedulesLinkClick: () -> Unit,
    val onMapIconClick: () -> Unit,
    val onSelectPrediction: (AutocompletePrediction) -> Unit,
    val onFavouritesTitleClick: () -> Unit,
    val onRecentTitleClick: () -> Unit,
    val onViewJourneySummary: (Int) -> Unit,
    val onStartJourney: (String, String) -> Unit
)

/**
 * Route composable for the Home screen
 *
 * Architecture Flow: Route → ViewModels → Repository → DAO → Database
 *
 * This route handles:
 * - Manual Dependency Injection: Creates Database → Repository → ViewModel chain locally
 * - Multiple ViewModels: CompletedJourneysViewModel for trip history, JourneyViewModel for navigation
 * - ViewModel Factory: Injects dependencies into ViewModels (required for constructor parameters)
 * - Lifecycle-aware State: Collects state flows that pause when screen is backgrounded
 * - Places API Integration: Manages location search and autocomplete through PlacesManager
 *
 * Each screen creates its own ViewModels locally, avoiding parameter passing complexity
 * while maintaining proper lifecycle scoping and state management.
 */
@Composable
fun HomeRoute(
    navController: NavController
) {
    val repository = App.appModule.repository

    // ViewModel for trip history and favorites management
    val completedJourneysViewModel: CompletedJourneysViewModel = viewModel(
        factory = viewModelFactory {
            CompletedJourneysViewModel()
        }
    )

    // Get user name from database (first user and only user)
    val userName by remember {
        repository.getAllUsers().map { users ->
            users.firstOrNull()?.name ?: "Commuter"
        }
    }.collectAsStateWithLifecycle(initialValue = "Commuter")


    // PlacesManager for Google Places API (search/autocomplete)
    val placesManager = App.appModule.placesManager
    val placesState by placesManager.state.collectAsStateWithLifecycle()
    val placesActions = placesManager.actions

    // JourneyViewModel scoped to Activity for shared navigation state
    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        })

    // Lifecycle-aware state collection
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()

    // Store just the ID
    var selectedTripId by remember { mutableStateOf<Int?>(null) }
    var selectedTripStartStop by remember { mutableStateOf<String?>(null) }
    var selectedTripEndStop by remember { mutableStateOf<String?>(null) }

    // Show dialog
    if (selectedTripId != null && selectedTripStartStop != null && selectedTripEndStop != null) {
        JourneyActionDialog(
            tripId = selectedTripId!!,
            startStop = selectedTripStartStop!!,
            endStop = selectedTripEndStop!!,
            onDismiss = {
                selectedTripId = null
                selectedTripStartStop = null
                selectedTripEndStop = null
            },
            onViewSummary = { tripId ->
                journeyViewModel.viewModelScope.launch {
                    journeyViewModel.loadJourneyForSummary(tripId)  // Cleanly passing tripId to get summary details
                }
                navController.navigate(Screen.JourneySummary.route)
                selectedTripId = null
                selectedTripStartStop = null
                selectedTripEndStop = null
            },
            onStartJourney = { startStop, endStop ->
                val startLocation = SelectedLocation(
                    latLng = LatLng(0.0, 0.0),
                    placeId = "",
                    primaryText = startStop
                )
                val destination = SelectedLocation(
                    latLng = LatLng(0.0, 0.0),
                    placeId = "",
                    primaryText = endStop
                )

                journeyViewModel.setStartLocation(startLocation)
                journeyViewModel.setDestination(destination)
                navController.navigate(Screen.JourneySelection.route)

                selectedTripId = null
                selectedTripStartStop = null
                selectedTripEndStop = null
            }
        )
    }

    // Handle location selection from autocomplete predictions
    val onSelectPrediction: (AutocompletePrediction) -> Unit =
        { prediction ->
            journeyViewModel.viewModelScope.launch {
                val selectedLocation = placesActions.getPlace(prediction)
                if (selectedLocation != null) {
                    journeyViewModel.confirmLocationSelection(
                        selectedLocation, LocationType.END, placesActions.onClearSearch
                    )
                }
            }
        }

    // Centralized action callbacks for all Home screen interactions
    val actions = HomeActions(
        completedJourneysViewModel::toggleFavourite,
        { navController.navigate(Screen.Schedules.route) },
        { navController.navigate(Screen.DestinationSelection.route) },
        onSelectPrediction,
        onFavouritesTitleClick = { navController.navigate(Screen.Favourites.route) },
        onRecentTitleClick = { navController.navigate(Screen.JourneyHistory.route) },
        onViewJourneySummary = { tripId ->
            journeyViewModel.viewModelScope.launch {
                journeyViewModel.loadJourneyForSummary(tripId)
            }
            navController.navigate(Screen.JourneySummary.route)
        },
        onStartJourney = { startStop, endStop ->
            val startLocation = SelectedLocation(
                latLng = LatLng(0.0, 0.0),
                placeId = "",
                primaryText = startStop
            )
            val destination = SelectedLocation(
                latLng = LatLng(0.0, 0.0),
                placeId = "",
                primaryText = endStop
            )
            journeyViewModel.setStartLocation(startLocation)
            journeyViewModel.setDestination(destination)
            navController.navigate(Screen.JourneySelection.route)
        }
    )

    // Render screen with bottom bar
    ScreenWrapper(navController, true, { paddingValues ->
        HomeContent(
            completedJourneysState = completedJourneysState, // Recent and favorite trips from ViewModel
            homeActions = actions,
            placesState = placesState,
            placesActions = placesActions,
            paddingValues = paddingValues,
            userName = userName,
        )
    })
}