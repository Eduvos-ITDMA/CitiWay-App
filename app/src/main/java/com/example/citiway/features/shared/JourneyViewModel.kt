package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.data.remote.SelectedLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class JourneyViewModel(val navController: NavController) : ViewModel() {
    private val _journeyState = MutableStateFlow(JourneyState())
    val journeyState: StateFlow<JourneyState> = _journeyState

    fun confirmLocationSelection(
        selectedLocation: SelectedLocation,
        locationType: LocationType,
        clearSearch: () -> Unit
    ) {
        when (locationType) {
            LocationType.START -> {
                _journeyState.update { it.copy(startLocation = selectedLocation) }
                clearSearch()
                navController.navigate(Screen.JourneySelection.route)
            }

            LocationType.END -> {
                _journeyState.update { it.copy(destination = selectedLocation) }
                clearSearch()
                navController.navigate(Screen.StartLocationSelection.route)
            }
        }
    }
}

data class JourneyState(
    var startLocation: SelectedLocation? = null,
    var destination: SelectedLocation? = null,
    // var selectedRoute: ...
)

enum class LocationType {
    START,
    END
}