package com.example.citiway.features.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.citiway.data.local.CitiWayDatabase
import com.example.citiway.data.repository.CitiWayRepository
import com.example.citiway.features.shared.OnboardingViewModel

@Composable
fun OnboardingRoute(
    navController: NavController
) {
    // Manual DI: Database → Repository → ViewModel
    val context = LocalContext.current
    val database = CitiWayDatabase.getDatabase(context)
    val repository = CitiWayRepository(database)

    // ViewModel with factory for constructor injection
    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = viewModelFactory {
            addInitializer(OnboardingViewModel::class) {
                OnboardingViewModel(repository = repository)
            }
        }
    )

    OnboardingContent(
        navController = navController,
        viewModel = onboardingViewModel
    )
}