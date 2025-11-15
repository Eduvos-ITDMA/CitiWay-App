package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.App
import com.example.citiway.core.utils.toLocalDateTime
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.data.local.JourneyOverview
import com.example.citiway.data.local.JourneyOverviewDTO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.collections.distinct
import kotlin.collections.filter
import kotlin.collections.map

/**
 * ViewModel for managing completed journeys and favourites
 *
 * Architecture Flow:
 * ViewModel → Repository → DAO → Room Database
 *
 * This ViewModel follows Clean Architecture principles by maintaining a clear separation
 * of concerns. It communicates exclusively with the Repository layer, which abstracts
 * all database operations through DAOs (Data Access Objects). This architecture ensures:
 * - Testability: Business logic is isolated from data layer implementation
 * - Maintainability: Database changes don't affect UI logic
 * - Scalability: Easy to swap data sources without changing ViewModel code
 */

data class CompletedJourneysState(
    val recentJourneys: List<JourneyOverview> = emptyList(),
    val favouriteJourneys: List<JourneyOverview> = emptyList(),
    val allJourneys: List<JourneyOverview> = emptyList(),
)

class CompletedJourneysViewModel(
    private val repository: CitiWayRepository = App.appModule.repository,
    private val currentUserId: Int = 1
) : ViewModel() {

    // Single source of truth for all journey-related UI state
    private val _screenState = MutableStateFlow(CompletedJourneysState())
    val screenState: StateFlow<CompletedJourneysState> = _screenState.asStateFlow()

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