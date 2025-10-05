package com.example.citiway.features.destination_selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.citiway.core.navigation.routes.Screen
import com.example.citiway.core.util.ScreenWrapper
import com.example.citiway.features.shared.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.LatLng
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import com.example.citiway.features.shared.DrawerViewModel
import com.example.citiway.features.shared.LocationSelectionViewModelFactory



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DestinationSelectionRoute(
    navController: NavController,
) {
    val context = LocalContext.current

    val drawerViewModel: DrawerViewModel = viewModel(
        viewModelStoreOwner = context as ComponentActivity
    )

    val mapViewModel: MapViewModel = viewModel(
        factory = LocationSelectionViewModelFactory(
            application = context.applicationContext as Application,
            drawerViewModel = drawerViewModel
        )
    )

    val state by mapViewModel.screenState.collectAsStateWithLifecycle()
    val actions = mapViewModel.actions

    val onConfirmLocation: (LatLng) -> Unit = { location ->
        // TODO: store selected location in shared view model
        navController.navigate(Screen.StartLocationSelection.route)
    }

    ScreenWrapper(navController, showBottomBar = true) { paddingValues ->
        DestinationSelectionContent(
            paddingValues = paddingValues,
            state = state,
            actions = actions,
            cameraPositionState = mapViewModel.cameraPositionState,
            onConfirmLocation = onConfirmLocation
        )
    }
}
