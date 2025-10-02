package com.example.citiway.core.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created a DataStore extension on Context to handle persistent storage. *Will move to app-level to share all prefs
 * Using DataStore instead of SharedPreferences because:
 * - DataStore is type-safe (prevents casting errors)
 * - Uses Kotlin Coroutines (non-blocking, won't freeze UI)
 * - Handles data corruption better
 * - Google's recommended modern solution for key-value storage
 *
 * This file is saved on device at: /data/data/com.example.citiway/files/datastore/settings.preferences_pb
 * Data persists across app restarts and is deleted only when app is uninstalled.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * SettingsManager handles all app settings that need to be saved permanently.
 * Used by the navigation drawer to persist toggle states like theme preferences.
 */
class SettingsManager(private val context: Context) {

    companion object {
        // Created unique keys to identify each setting in storage
        // These act like addresses to find specific values in the DataStore
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LOCATION_ENABLED_KEY = booleanPreferencesKey("location_enabled")
        private val MYCITI_ENABLED_KEY = booleanPreferencesKey("myciti_enabled")
    }

    /**
     * Getting a Flow that emits the current dark mode state.
     * Flow automatically notifies observers when the value changes.
     * .map is extracting just the dark_mode value from all stored settings.
     * Using ?: false as default when no value has been saved yet.
     */
    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    /**
     * Getting a Flow that emits whether location services are enabled.
     * Defaulting to true since location is typically needed for the app.
     */
    val locationEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[LOCATION_ENABLED_KEY] ?: true
        }

    /**
     * Getting a Flow that emits whether MyCiti bus integration is enabled.
     * Defaulting to false as this is an optional feature.
     */
    val myCitiEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[MYCITI_ENABLED_KEY] ?: false
        }

    /**
     * Saving the dark mode preference to disk.
     * Marked as suspend because writing to storage takes time and runs asynchronously.
     * This prevents the UI from freezing while waiting for the save operation.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    /**
     * Saving the location enabled preference to disk.
     * Called when user toggles location services in settings.
     */
    suspend fun setLocationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LOCATION_ENABLED_KEY] = enabled
        }
    }

    /**
     * Saving the MyCiti integration preference to disk.
     * Called when user toggles MyCiti features in settings.
     */
    suspend fun setMyCitiEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MYCITI_ENABLED_KEY] = enabled
        }
    }
}