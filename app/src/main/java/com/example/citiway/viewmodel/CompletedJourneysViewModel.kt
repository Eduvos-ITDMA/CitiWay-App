package com.example.citiway.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.data.CompletedJourney
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class CompletedJourneysViewModel : ViewModel() {
    // All Journeys list state flow
    private val _journeys = MutableStateFlow<List<CompletedJourney>>(emptyList())
    val journeys: StateFlow<List<CompletedJourney>> = _journeys

    // Recent Journeys list state flow
    private val _recentJourneys = MutableStateFlow<List<CompletedJourney>>(emptyList())
    val recentJourneys: StateFlow<List<CompletedJourney>> = _recentJourneys

    // Favourite Journeys list state flow
    private val _favouriteJourneys = MutableStateFlow<List<CompletedJourney>>(emptyList())
    val favouriteJourneys: StateFlow<List<CompletedJourney>> = _favouriteJourneys

    init {
        // Test data
        // This will eventually be replaced with a call to the database (repository)
        _journeys.value = listOf(
            CompletedJourney("1", "Claremont to Cape Town", LocalDate.of(2025, 12, 25), 50, true),
            CompletedJourney("2", "Town to Suburb", LocalDate.of(2025, 11, 20), 35, false)
        )

        viewModelScope.launch {
            // Use .collect() to recompute derived lists any time the source list changes
            _journeys.collect { all ->
                _favouriteJourneys.value = all.filter { it.isFavourite }

                // recent: top 3 most recent trips and not older than 4 months
                val fourMonthsAgo = LocalDate.now().minusMonths(4)
                _recentJourneys.value = all
                    .filter { it.date.isAfter(fourMonthsAgo) }
                    .sortedByDescending { it.date }
                    .take(3)
            }
        }
    }

    //fun toggleFavourite(id: String) {
    //    _recentJourneys.update { list ->
    //        list.map { journey ->
    //            if (journey.id == id) journey.copy(isFavourite = !journey.isFavourite) else journey
    //        }
    //    }

    //    _favouriteJourneys.update { list ->
    //        list.map { journey ->
    //            if (journey.id == id) journey.copy(isFavourite = !journey.isFavourite) else journey
    //        }
    //    }
    //}

    fun toggleFavourite(id: String) {
        _journeys.value = _journeys.value.map { j ->
            if (j.id == id) j.copy(isFavourite = !j.isFavourite) else j
        }
    }
}
