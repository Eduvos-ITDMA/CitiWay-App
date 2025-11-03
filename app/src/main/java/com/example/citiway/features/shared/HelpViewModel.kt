package com.example.citiway.features.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.App
import com.example.citiway.data.repository.CitiWayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HelpScreenState(
    val userEmail: String = "",
    val userName: String = "",
    val isLoading: Boolean = true
)

class HelpViewModel(
    private val repository: CitiWayRepository = App.appModule.repository
) : ViewModel() {

    private val _screenState = MutableStateFlow(HelpScreenState())
    val screenState: StateFlow<HelpScreenState> = _screenState.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            try {
                val user = repository.getFirstUser()
                _screenState.value = _screenState.value.copy(
                    userEmail = user?.email ?: "",
                    userName = user?.name ?: "",
                    isLoading = false
                )
            } catch (e: Exception) {
                _screenState.value = _screenState.value.copy(
                    userEmail = "",
                    userName = "",
                    isLoading = false
                )
            }
        }
    }
}