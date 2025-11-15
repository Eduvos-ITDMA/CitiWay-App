package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.data.local.DatabaseSeeder
import com.example.citiway.data.local.entities.UserEntity
import com.example.citiway.data.repository.CitiWayRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: CitiWayRepository
) : ViewModel() {

    fun saveUser(
        name: String,
        email: String,
        preferredLanguage: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 1. Creating and inserting the user with repo
                val user = UserEntity(
                    name = name,
                    email = email,
                    preferred_language = preferredLanguage,
                    created_at = System.currentTimeMillis()
                )

                repository.insertUser(user)
                println("✅ User saved: $name ($email)")

                // 2. Seeding the database with test data straight after onboarding
                val seeder = DatabaseSeeder(repository)
                seeder.seedDatabase()

                // 3. Navigate to home screen
                onSuccess()
            } catch (e: Exception) {
                println("❌ Error in onboarding: ${e.message}")
                onError(e.message ?: "Failed to save user")
            }
        }
    }
}