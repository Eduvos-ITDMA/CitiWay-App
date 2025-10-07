package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.data.remote.SelectedLocation

class JourneyViewModel(val navController: NavController) : ViewModel() {

    private val journeySelectionOptions = JourneySelectionOptions()

    fun confirmLocationSelection(
        selectedLocation: SelectedLocation,
        locationType: LocationType,
        clearSearch: () -> Unit
    ) {
        when (locationType) {
            LocationType.START -> {
                journeySelectionOptions.startLocation = selectedLocation
                clearSearch()
                navController.navigate(Screen.JourneySelection.route)
            }

            LocationType.END -> {
                journeySelectionOptions.destination = selectedLocation
                clearSearch()
                navController.navigate(Screen.StartLocationSelection.route)
            }
        }
    }
}

data class JourneySelectionOptions(
    var startLocation: SelectedLocation? = null,
    var destination: SelectedLocation? = null,
    // var selectedRoute: ...
)

enum class LocationType {
    START,
    END
}