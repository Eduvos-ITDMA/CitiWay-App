package com.example.citiway.features.stats

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.App
import com.example.citiway.data.repository.CitiWayRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsViewModel(
    private val repository: CitiWayRepository = App.appModule.repository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _state = MutableStateFlow(StatsState())
    val state: StateFlow<StatsState> = _state

    init {
        loadMonthlyStats()
    }

    /**
     * Loads all monthly statistics from the database
     */
    fun loadMonthlyStats() {
        viewModelScope.launch(dispatcher) {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = 1 // TODO: Get from actual auth system
                val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))

                // Fetch all data in parallel
                val busStats = repository.getTransportStats(
                    userId = userId,
                    providerId = 1, // MyCiti Bus
                    month = currentMonth
                )

                val trainStats = repository.getTransportStats(
                    userId = userId,
                    providerId = 2, // Metrorail
                    month = currentMonth
                )

                val walkingDistance = repository.getTotalWalkingDistance(
                    userId = userId,
                    month = currentMonth
                )

                val totalSpent = (busStats.totalFare ?: 0.0) + (trainStats.totalFare ?: 0.0)

                _state.update {
                    it.copy(
                        isLoading = false,
                        totalSpent = totalSpent,
                        monthlyBudget = 400.0, // TODO: Make this user-configurable
                        busSpent = busStats.totalFare ?: 0.0,
                        busDistance = busStats.totalDistance ?: 0.0,
                        trainSpent = trainStats.totalFare ?: 0.0,
                        trainDistance = trainStats.totalDistance ?: 0.0,
                        walkingDistanceMeters = walkingDistance,
                        currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                    )
                }

                Log.d("StatsViewModel", "✅ Stats loaded: Bus R${busStats.totalFare}, Train R${trainStats.totalFare}")

            } catch (e: Exception) {
                Log.e("StatsViewModel", "❌ Failed to load stats: ${e.message}", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load statistics"
                    )
                }
            }
        }
    }

    /**
     * Refreshes the stats data
     */
    fun refresh() {
        loadMonthlyStats()
    }
}

data class StatsState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val totalSpent: Double = 0.0,
    val monthlyBudget: Double = 800.0,
    val busSpent: Double = 0.0,
    val busDistance: Double = 0.0,
    val trainSpent: Double = 0.0,
    val trainDistance: Double = 0.0,
    val walkingDistanceMeters: Int = 0,
    val currentMonth: String = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"))
)

/**
 * Data class for transport statistics
 */
data class TransportStats(
    val totalFare: Double?,
    val totalDistance: Double?
)