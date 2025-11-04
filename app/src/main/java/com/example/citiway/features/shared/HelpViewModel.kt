package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.App
import com.example.citiway.data.repository.CitiWayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state holder for the Help screen.
 *
 * @property userEmail The email address of the current user
 * @property userName The display name of the current user
 * @property isLoading Loading indicator for data fetching operations
 */
data class HelpScreenState(
    val userEmail: String = "",
    val userName: String = "",
    val isLoading: Boolean = true
)

/**
 * ViewModel for managing Help screen state and business logic.
 *
 * Handles fetching and exposing user information needed for support
 * and help functionality, such as pre-filling contact forms.
 *
 * @property repository Repository for accessing user data
 */
class HelpViewModel(
    private val repository: CitiWayRepository = App.appModule.repository
) : ViewModel() {

    private val _screenState = MutableStateFlow(HelpScreenState())
    val screenState: StateFlow<HelpScreenState> = _screenState.asStateFlow()

    init {
        loadUserInfo()  //Initialisation
    }

    /**
     * Loads the current user's information from the repository.
     *
     * Fetches the first user record and updates the screen state with
     * their email and name. If no user is found or an error occurs,
     * the state is updated with empty values and loading is set to false.
     */
    private fun loadUserInfo() {
        viewModelScope.launch {
            try {
                // Fetch the first user from local database
                val user = repository.getFirstUser()

                // Update state with user information or empty values if null
                _screenState.value = _screenState.value.copy(
                    userEmail = user?.email ?: "",
                    userName = user?.name ?: "",
                    isLoading = false
                )
            } catch (e: Exception) {
                // Handle errors gracefully by clearing user data
                _screenState.value = _screenState.value.copy(
                    userEmail = "",
                    userName = "",
                    isLoading = false
                )
            }
        }
    }
}