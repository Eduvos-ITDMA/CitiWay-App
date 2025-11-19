package com.example.citiway.features.home

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.ScreenWrapper
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
    val onRepeatJourney: (LatLng, LatLng) -> Unit
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

    // ViewModel for trip history and favorites management
    val completedJourneysViewModel: CompletedJourneysViewModel = viewModel(
        factory = viewModelFactory {
            CompletedJourneysViewModel(
                placesActions = placesActions,
                journeyViewModel = journeyViewModel,
                navController = navController
            )
        }
    )

    // Lifecycle-aware state collection
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()
    val completedJourneysActions = completedJourneysViewModel.actions

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
        onViewJourneySummary = completedJourneysActions.onViewJourneySummary,
        onRepeatJourney = completedJourneysActions.onRepeatJourney
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