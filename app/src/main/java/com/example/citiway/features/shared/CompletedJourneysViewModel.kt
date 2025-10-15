package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.data.local.entities.Trip
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.data.local.CompletedJourney
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CompletedJourneysState(
    val recentJourneys: List<CompletedJourney> = emptyList(),
    val favouriteJourneys: List<CompletedJourney> = emptyList(),
    val allJourneys: List<CompletedJourney> = emptyList(),
    val allFavouriteJourneys: List<CompletedJourney> = emptyList()
)

class CompletedJourneysViewModel(
    private val repository: CitiWayRepository,
    private val currentUserId: Int = 1
) : ViewModel() {

    private val _screenState = MutableStateFlow(CompletedJourneysState())
    val screenState: StateFlow<CompletedJourneysState> = _screenState.asStateFlow()

    init {
        loadJourneys()
        loadAllJourneys()
    }

    private fun loadJourneys() {
        viewModelScope.launch {
            repository.getRecentTrips(currentUserId, limit = 20).collectLatest { trips ->
                val journeys = trips.map { it.toCompletedJourney() }
                _screenState.update { it.copy(recentJourneys = journeys) }
            }
        }
    }

    private fun loadAllJourneys() {
        viewModelScope.launch {
            repository.getAllTripsForUser(currentUserId).collectLatest { trips ->
                val journeys = trips.map { it.toCompletedJourney() }
                _screenState.update { it.copy(allJourneys = journeys) }
            }
        }
    }

    fun toggleFavourite(journeyId: String) {
        viewModelScope.launch {
            try {
                val id = journeyId.toIntOrNull() ?: return@launch
                println("⚠️ Favorite toggle not yet implemented - need to add isFavorite to Trip table")
            } catch (e: Exception) {
                println("❌ Error toggling favorite: ${e.message}")
            }
        }
    }

    fun saveJourney(
        startStop: String?,
        endStop: String?,
        date: String,
        tripTime: String,
        totalFare: Double
    ) {
        viewModelScope.launch {
            try {
                val trip = Trip(
                    user_id = currentUserId,
                    start_stop = startStop,
                    end_stop = endStop,
                    date = date,
                    trip_time = tripTime,
                    total_fare = totalFare
                )
                repository.insertTrip(trip)
                println("✅ Saved journey: $date - R$totalFare")
            } catch (e: Exception) {
                println("❌ Error saving journey: ${e.message}")
            }
        }
    }

    private fun Trip.toCompletedJourney(): CompletedJourney {
        return CompletedJourney(
            id = this.trip_id.toString(),
            route = "${this.start_stop ?: "Start"} → ${this.end_stop ?: "End"}",
            date = this.date ?: "",
            durationMin = this.trip_time?.replace(" min", "")?.toIntOrNull() ?: 0,
            isFavourite = false
        )
    }
}