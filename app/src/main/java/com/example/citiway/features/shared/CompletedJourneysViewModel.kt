package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citiway.data.local.SavedPlace
import com.citiway.data.local.SavedPlaceDao
import com.example.citiway.data.local.CompletedJourney
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CompletedJourneysState(
    val recentJourneys: List<CompletedJourney> = emptyList(),
    val favouriteJourneys: List<CompletedJourney> = emptyList(),
    val allJourneys: List<CompletedJourney> = emptyList(), // NEW val for loading all db entries for testing history and favorites
    val allFavouriteJourneys: List<CompletedJourney> = emptyList() // NEW val for loading all db entries for testing history and favorites
)

class CompletedJourneysViewModel(
    private val savedPlaceDao: SavedPlaceDao
) : ViewModel() {

    private val _screenState = MutableStateFlow(CompletedJourneysState())
    val screenState: StateFlow<CompletedJourneysState> = _screenState.asStateFlow()

    init {
        loadJourneys()
        loadAllJourneys()
        loadAllFavouriteJourneys()
    }

    private fun loadJourneys() {
        viewModelScope.launch {
            // Load recent journeys
            savedPlaceDao.getRecentJourneys().collectLatest { savedPlaces ->
                val journeys = savedPlaces.map { it.toCompletedJourney() }
                _screenState.update { it.copy(recentJourneys = journeys) }
            }
        }

        viewModelScope.launch {
            // Load favorite journeys
            savedPlaceDao.getFavoriteJourneys().collectLatest { savedPlaces ->
                val journeys = savedPlaces.map { it.toCompletedJourney() }
                _screenState.update { it.copy(favouriteJourneys = journeys) }
            }
        }
    }

    private fun loadAllJourneys() {
        viewModelScope.launch {
            savedPlaceDao.getAllJourneys().collectLatest { savedPlaces ->
                val journeys = savedPlaces.map { it.toCompletedJourney() }
                _screenState.update { it.copy(allJourneys = journeys) } // reuse existing field
            }
        }
    }

    private fun loadAllFavouriteJourneys() {
        viewModelScope.launch {
            savedPlaceDao.getAllFavouriteJourneys().collectLatest { savedPlaces ->
                val favourites = savedPlaces.map { it.toCompletedJourney() }
                _screenState.update { it.copy(allFavouriteJourneys = favourites) }
            }
        }
    }

    fun toggleFavourite(journeyId: String) {
        viewModelScope.launch {
            try {
                val id = journeyId.toIntOrNull() ?: return@launch

                // Get current place from database
                val place = savedPlaceDao.getPlaceById(id) ?: return@launch

                // Toggle favorite status
                savedPlaceDao.updateFavoriteStatus(id, !place.isFavorite)

                println("✅ Toggled favorite for journey $id to ${!place.isFavorite}")
            } catch (e: Exception) {
                println("❌ Error toggling favorite: ${e.message}")
            }
        }
    }

    // Helper function to save a journey (call this when user completes a journey) ** will do after Routes api is working
    fun saveJourney(route: String, date: String, durationMin: Int, isFavorite: Boolean = false) {
        viewModelScope.launch {
            val journey = SavedPlace(
                route = route,
                journeyDate = date,
                durationMin = durationMin,
                isFavorite = isFavorite,
                itemType = "journey",
                lastUsedTimestamp = System.currentTimeMillis()
            )
            savedPlaceDao.insertPlace(journey)
            println("✅ Saved journey: $route")
        }
    }

    // Extension function to convert SavedPlace to CompletedJourney
    private fun SavedPlace.toCompletedJourney(): CompletedJourney {
        return CompletedJourney(
            id = this.id.toString(),
            route = this.route ?: "",
            date = this.journeyDate ?: "",
            durationMin = this.durationMin ?: 0,
            isFavourite = this.isFavorite
        )
    }
}