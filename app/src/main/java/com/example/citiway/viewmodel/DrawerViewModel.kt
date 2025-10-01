package com.example.citiway.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.citiway.ui.theme.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * DrawerViewModel acts as a middleman between the drawer UI and saved settings.
 *
 * Why we need this:
 * - Survives screen rotation (UI gets destroyed and rebuilt, ViewModel doesn't)
 * - Handles all the async/coroutine complexity for the UI
 * - Keeps the drawer composable clean and simple
 *
 * Extended AndroidViewModel (not just ViewModel) because we needed Application context
 * to create the SettingsManager.
 */
class DrawerViewModel(application: Application) : AndroidViewModel(application) {

    // Created our connection to the settings storage
    // This is the "assistant" that talks to the "filing cabinet"
    private val settingsManager = SettingsManager(application)

    /**
     * Converted the dark mode Flow into a StateFlow that the UI can easily read.
     *
     * stateIn() does three important things:
     * 1. Gives us the CURRENT value instantly (not just listening for changes)
     * 2. Keeps it alive while UI is watching (WhileSubscribed)
     * 3. Waits 5 seconds after UI stops watching before cleaning up (handles quick rotations)
     *
     * initialValue = what to show before DataStore finishes loading
     */
    val darkModeEnabled = settingsManager.darkModeFlow.stateIn(
        scope = viewModelScope,  // Tied to ViewModel's life - auto cleanup when ViewModel dies
        started = SharingStarted.WhileSubscribed(5000),  // Stay active for 5 seconds after last subscriber
        initialValue = false  // Show this immediately while loading real value
    )

    /**
     * Getting the current location setting as a StateFlow.
     * Starting with true because location is usually needed by default.
     */
    val locationEnabled = settingsManager.locationEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    /**
     * Getting the MyCiti feature toggle as a StateFlow.
     * Starting with false because it's an optional feature.
     */
    val myCitiEnabled = settingsManager.myCitiEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    /**
     * Called when user toggles dark mode switch in the drawer.
     *
     * viewModelScope.launch creates a coroutine (background task) because
     * settingsManager.setDarkMode() is a suspend function that saves to disk.
     * This prevents the UI from freezing while waiting for the save.
     */
    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setDarkMode(enabled)
        }
    }

    /**
     * Called when user toggles location switch.
     * Launched in a coroutine to save asynchronously without blocking UI.
     */
    fun toggleLocation(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setLocationEnabled(enabled)
        }
    }

    /**
     * Called when user toggles MyCiti integration.
     * Launched in a coroutine to save asynchronously without blocking UI.
     */
    fun toggleMyCiti(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setMyCitiEnabled(enabled)
        }
    }
}