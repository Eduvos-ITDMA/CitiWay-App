package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.data.local.entities.User
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
                val user = User(
                    name = name,
                    email = email,
                    preferred_language = preferredLanguage,
                    created_at = System.currentTimeMillis()
                )

                repository.insertUser(user)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to save user")
            }
        }
    }
}