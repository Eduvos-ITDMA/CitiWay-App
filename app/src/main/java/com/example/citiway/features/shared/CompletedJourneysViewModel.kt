package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.data.local.CompletedJourney
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CompletedJourneysState(
    val journeys: List<CompletedJourney> = emptyList(),
    val recentJourneys: List<CompletedJourney> = emptyList(),
    val favouriteJourneys: List<CompletedJourney> = emptyList()
)

class CompletedJourneysViewModel : ViewModel() {
    // This _journeys state flow gets updated by this ViewModel's event methods, triggering
    // updates to the ScreenState state flow
    private val _journeys = MutableStateFlow<List<CompletedJourney>>(emptyList())

    // State flow for main state data class
    private val _screenState = MutableStateFlow(CompletedJourneysState())
    val screenState: StateFlow<CompletedJourneysState> = _screenState

    init {
        viewModelScope.launch {
            // Use .collect() to recompute derived lists any time the source list changes
            _journeys.collect { all ->
                val favouriteJourneys = all.filter { it.isFavourite }

                // recent: top 3 most recent trips and not older than 4 months
                val fourMonthsAgo = LocalDate.now().minusMonths(4)
                val recentJourneys = all
                    .filter { it.date.isAfter(fourMonthsAgo) }
                    .sortedByDescending { it.date }
                    .take(3)

                // Atomically update the single screen state
                _screenState.update { currentState ->
                    currentState.copy(
                        journeys = all,
                        recentJourneys = recentJourneys,
                        favouriteJourneys = favouriteJourneys
                    )
                }
            }
        }

        // Test data
        // This will eventually be replaced with a call to the database (repository)
        val testJourneys = listOf(
            CompletedJourney("1", "Claremont to Cape Town", LocalDate.of(2025, 12, 25), 50, true),
            CompletedJourney("2", "Town to Suburb", LocalDate.of(2025, 11, 20), 35, false)
        )

        _screenState.update { currentState ->
            currentState.copy(journeys = testJourneys)
        }
    }

    fun toggleFavourite(id: String) {
        _journeys.value = _journeys.value.map { j ->
            if (j.id == id) j.copy(isFavourite = !j.isFavourite) else j
        }
    }
}