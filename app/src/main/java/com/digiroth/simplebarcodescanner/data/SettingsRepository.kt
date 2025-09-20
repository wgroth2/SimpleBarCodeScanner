/* Copyright 2025 Bill Roth */
package com.digiroth.simplebarcodescanner.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Create a singleton instance of DataStore for the entire app
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * A repository for managing user settings, powered by Jetpack DataStore.
 * This class is the single source of truth for all app settings.
 */
class SettingsRepository(private val context: Context) {

    // Define the keys for your preferences. This provides type safety.
    private object PreferenceKeys {
        val AUTO_ZOOM_ENABLED = booleanPreferencesKey("auto_zoom")
    }

    /**
     * A flow that emits the current auto-zoom preference. It will automatically
     * emit a new value whenever the preference changes.
     * Defaults to 'true' if the preference is not yet set.
     */
    val isAutoZoomEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.AUTO_ZOOM_ENABLED] ?: true // Default value
        }

    /**
     * Updates the auto-zoom preference. This is a suspend function, ensuring
     * it is called from a coroutine and does not block the UI thread.
     *
     * @param isEnabled The new value for the auto-zoom setting.
     */
    suspend fun setAutoZoomEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.AUTO_ZOOM_ENABLED] = isEnabled
        }
    }
}
