package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.toLocalDateTime
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.data.local.JourneyOverview
import com.example.citiway.data.local.JourneyOverviewDTO
import com.example.citiway.data.remote.PlacesActions
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.collections.distinct
import kotlin.collections.filter
import kotlin.collections.map

data class CompletedJourneysActions(
    val onToggleFavourite: (JourneyOverview) -> Unit,
    val onViewJourneySummary: (Int) -> Unit,
    val onRepeatJourney: (LatLng, LatLng) -> Unit,
)

data class CompletedJourneysState(
    val recentJourneys: List<JourneyOverview> = emptyList(),
    val favouriteJourneys: List<JourneyOverview> = emptyList(),
    val allJourneys: List<JourneyOverview> = emptyList(),
)

/**
 * ViewModel for managing completed journeys and favourites
 */

class CompletedJourneysViewModel(
    private val placesActions: PlacesActions,
    private val journeyViewModel: JourneyViewModel,
    private val navController: NavController,
    private val currentUserId: Int = 1,
    private val repository: CitiWayRepository = App.appModule.repository,
) : ViewModel() {

    // Single source of truth for all journey-related UI state
    private val _screenState = MutableStateFlow(CompletedJourneysState())
    val screenState: StateFlow<CompletedJourneysState> = _screenState.asStateFlow()

    val actions = CompletedJourneysActions(
        onToggleFavourite = ::toggleFavourite,
        onViewJourneySummary = ::viewJourneySummary,
        onRepeatJourney = ::repeatJourney
    )

    init {
        loadRecentJourneys()
        loadAllJourneys()
        loadFavouriteJourneys()
    }

    /**
     * Loads recent journeys for the home page
     */
    private fun loadRecentJourneys() {
        viewModelScope.launch {
            // Load recent journeys (last 2 trips) (HOME PAGE)
            repository.getRecentJourneyOverview(currentUserId, limit = 3).collectLatest { trips ->
                val journeys = trips.map { it.toJourneyOverview() }
                _screenState.update { it.copy(recentJourneys = journeys) }
            }
        }
    }

    /**
     * Loads all journeys for the trip history page
     */
    private fun loadAllJourneys() {
        viewModelScope.launch {
            val journeys =
                repository.getJourneyOverviewsByUserId(currentUserId).map { it.toJourneyOverview() }
            _screenState.update { it.copy(allJourneys = journeys) }
        }
    }

    /**
     * Loads favourite journeys for quick access
     */
    private fun loadFavouriteJourneys() {
        viewModelScope.launch {
            repository.getFavouriteJourneyOverview(currentUserId).collectLatest { trips ->
                _screenState.update { currentState ->
                    currentState.copy(
                        favouriteJourneys = trips.map { it.toJourneyOverview() }
                    )
                }
            }
        }
    }

    fun viewJourneySummary(id: Int) {
        navController.navigate(Screen.JourneySummary.createRoute(id))
    }

    fun repeatJourney(startLatLng: LatLng, destLatLng: LatLng) {
        viewModelScope.launch {
            // Use coroutineScope to wait for both async calls to complete
            coroutineScope {
                val startLocationDeferred =
                    async { placesActions.getPlaceFromLatLng(startLatLng) }
                val destinationDeferred = async { placesActions.getPlaceFromLatLng(destLatLng) }

                val startLocation = startLocationDeferred.await()
                val destination = destinationDeferred.await()

                journeyViewModel.setStartLocation(startLocation)
                journeyViewModel.setDestination(destination)
                navController.navigate(Screen.JourneySelection.route)
            }
        }
    }

    /**
     * Toggles the favourite status of a journey
     */
    fun toggleFavourite(journey: JourneyOverview) {
        viewModelScope.launch {
            try {
                val id = journey.id

                // Toggle favourite status and persist to database
                repository.toggleFavouriteJourney(id, !journey.isFavourite)

                println("✅ Toggled favourite for trip $id to ${!journey.isFavourite}")
            } catch (e: Exception) {
                println("❌ Error toggling favourite: ${e.message}")
            }
        }
    }

    /**
     * Extension function to convert database Trip entity to UI model
     */
    private fun JourneyOverviewDTO.toJourneyOverview(): JourneyOverview {
        val modes = this.instructions
            .map { it.travelMode }
            .filter { it != "WALK" }
            .distinct()

        val tripMode = when {
            modes.isEmpty() -> "Walk"
            modes.size == 1 -> when (modes[0]) {
                "HEAVY_RAIL" -> "Train"
                "BUS" -> "Bus"
                else -> "Other"
            }

            else -> "Multi"
        }

        return JourneyOverview(
            id = this.journey_id,
            route = "${this.start_location_name} | ${this.destination_name}",
            startLocationLatLng = this.start_location_latlng,
            destinationLatLng = this.destination_latlng,
            date = this.start_time.toLocalDateTime().toLocalDate().toString(),
            durationMin = Duration.between(this.arrival_time, this.start_time).toMinutes().toInt(),
            mode = tripMode,
            isFavourite = this.is_favourite
        )
    }
}