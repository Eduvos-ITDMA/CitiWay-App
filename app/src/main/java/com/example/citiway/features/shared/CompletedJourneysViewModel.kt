package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.App
import com.example.citiway.data.local.entities.Trip
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.data.local.CompletedJourney
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing completed journeys and favorites
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
 *
 * Key Features:
 * 1. Journey Management: Loads recent, all, and favorite journeys
 * 2. Real-time Updates: Uses Kotlin Flow for reactive data streaming
 * 3. Favorite Toggle: Allows users to mark/unmark journeys as favorites
 * 4. State Management: Centralized UI state through StateFlow
 */

data class CompletedJourneysState(
    val recentJourneys: List<CompletedJourney> = emptyList(),
    val favouriteJourneys: List<CompletedJourney> = emptyList(),
    val allJourneys: List<CompletedJourney> = emptyList(),
    val allFavouriteJourneys: List<CompletedJourney> = emptyList()
)

class CompletedJourneysViewModel(
    private val repository: CitiWayRepository = App.appModule.repository,
    private val currentUserId: Int = 1
) : ViewModel() {

    // Single source of truth for all journey-related UI state
    private val _screenState = MutableStateFlow(CompletedJourneysState())
    val screenState: StateFlow<CompletedJourneysState> = _screenState.asStateFlow()

    init {
        loadJourneys()
        loadAllJourneys()
        loadFavoriteJourneys()
    }

    /**
     * Loads recent journeys for the home page
     *
     * Flow: ViewModel → Repository.getRecentTrips() → TripDao.getRecentTrips() → Room DB
     *
     * Uses Flow to observe database changes in real-time. When a new trip is added or
     * an existing trip is updated, the UI automatically reflects these changes without
     * requiring manual refresh.
     */
    private fun loadJourneys() {
        viewModelScope.launch {
            // Load recent journeys (last 2 trips) (HOME PAGE)
            repository.getRecentTrips(currentUserId, limit = 3).collectLatest { trips ->
                val journeys = trips.map { it.toCompletedJourney() }
                _screenState.update { it.copy(recentJourneys = journeys) }
            }
        }
    }

    /**
     * Loads all journeys for the trip history page
     *
     * Flow: ViewModel → Repository.getAllTripsForUser() → TripDao.getAllTripsForUser() → Room DB
     *
     * Retrieves complete journey history for the current user, enabling comprehensive
     * trip tracking and analysis.
     */
    private fun loadAllJourneys() {
        viewModelScope.launch {
            // Load all journeys for this user (Trip History PAGE)
            repository.getAllTripsForUser(currentUserId).collectLatest { trips ->
                val journeys = trips.map { it.toCompletedJourney() }
                _screenState.update { it.copy(allJourneys = journeys) }
            }
        }
    }

    /**
     * Loads favorite journeys for quick access
     *
     * Flow: ViewModel → Repository.getFavoriteTrips() → TripDao.getFavoriteTrips() → Room DB
     *
     * Maintains two lists:
     * - favouriteJourneys: Limited to 2 for home page display
     * - allFavouriteJourneys: Complete list for dedicated favorites section
     *
     * This dual approach optimizes UI performance while providing comprehensive access.
     */
    private fun loadFavoriteJourneys() {
        viewModelScope.launch {
            // Load favorite journeys
            repository.getFavoriteTrips(currentUserId).collectLatest { trips ->
                val journeys = trips.map { it.toCompletedJourney() }
                _screenState.update {
                    it.copy(
                        favouriteJourneys = journeys.take(3), // HOME PAGE LIMITING to 2
                        allFavouriteJourneys = journeys
                    )
                }
            }
        }
    }

    /**
     * Toggles the favorite status of a journey
     *
     * Flow: ViewModel → Repository.getTripById() → TripDao.getTripById() → Room DB
     *       ViewModel → Repository.toggleFavoriteTrip() → TripDao.updateTrip() → Room DB
     *
     * How it works:
     * 1. Converts journey ID from String to Int
     * 2. Fetches current trip from database via Repository
     * 3. Inverts the is_favourite boolean value
     * 4. Updates database through Repository layer
     * 5. Flow automatically propagates changes to UI through loadFavoriteJourneys()
     *
     * The Repository acts as a clean abstraction layer, ensuring this ViewModel remains
     * independent of database implementation details.
     */
    fun toggleFavourite(journeyId: String) {
        viewModelScope.launch {
            try {
                val id = journeyId.toIntOrNull() ?: return@launch

                // Get current trip from database
                val trip = repository.getTripById(id) ?: return@launch

                // Toggle favorite status and persist to database
                repository.toggleFavoriteTrip(id, !trip.is_favourite)

                println("✅ Toggled favorite for trip $id to ${!trip.is_favourite}")
            } catch (e: Exception) {
                println("❌ Error toggling favorite: ${e.message}")
            }
        }
    }

    /**
     * Saves a new completed journey to the database
     *
     * Flow: ViewModel → Repository.insertTrip() → TripDao.insertTrip() → Room DB
     *
     * Creates a Trip entity with all necessary fields and persists it through the
     * Repository layer. The saved journey will automatically appear in relevant
     * lists due to Flow-based observation.
     */
    fun saveJourney(
        startStop: String?,
        endStop: String?,
        date: String,
        tripTime: String,
        mode: String = "Bus",
        totalDistance: Double = 0.0,
        totalFare: Double,
        isFavorite: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                val trip = Trip(
                    user_id = currentUserId,
                    start_stop = startStop,
                    end_stop = endStop,
                    date = date,
                    trip_time = tripTime,
                    mode = mode,
                    total_distance_km = totalDistance,
                    total_fare = totalFare,
                    is_favourite = isFavorite,
                    created_at = System.currentTimeMillis()
                )

                repository.insertTrip(trip)
                println("✅ Saved journey: $date - R$totalFare - Mode: $mode")
            } catch (e: Exception) {
                println("❌ Error saving journey: ${e.message}")
            }
        }
    }

    /**
     * Extension function to convert database Trip entity to UI model
     *
     * Transforms Room entity into a presentation-friendly model, following the
     * separation of concerns principle by keeping database models separate from UI models.
     */
    private fun Trip.toCompletedJourney(): CompletedJourney {
        return CompletedJourney(
            id = this.trip_id.toString(),
            route = "${this.start_stop ?: "Start"} | ${this.end_stop ?: "End"}",
            date = this.date ?: "",
            durationMin = this.trip_time?.replace(" min", "")?.toIntOrNull() ?: 0,
            mode = this.mode ?: "",
            isFavourite = this.is_favourite
        )
    }
}