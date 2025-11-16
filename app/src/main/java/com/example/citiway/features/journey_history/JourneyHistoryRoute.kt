package com.example.citiway.features.journey_history

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.App
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.utils.ScreenWrapper
import com.example.citiway.data.remote.PlacesActions
import com.example.citiway.di.viewModelFactory
import com.example.citiway.features.shared.CompletedJourneysViewModel
import com.example.citiway.features.shared.JourneyViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun JourneyHistoryRoute(
    navController: NavController
) {
    val placesActions = App.appModule.placesManagerFactory.create().actions

    // ViewModels with factory for constructor injection
    val journeyViewModel: JourneyViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = viewModelFactory {
            JourneyViewModel(navController)
        })

    val completedJourneysViewModel: CompletedJourneysViewModel = viewModel(
        factory = viewModelFactory {
            CompletedJourneysViewModel(
                placesActions = placesActions,
                journeyViewModel = journeyViewModel,
                navController = navController
            )
        })

    // Lifecycle-aware state collection (stops when backgrounded)
    val completedJourneysState by completedJourneysViewModel.screenState.collectAsStateWithLifecycle()

    // Render screen with all journey history
    ScreenWrapper(navController, showBottomBar = true, content = { paddingValues ->
        JourneyHistoryContent(
            journeys = completedJourneysState.allJourneys,
            paddingValues = paddingValues,
            actions = completedJourneysViewModel.actions
        )
    })
}