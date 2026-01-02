package com.example.myapp017christmasapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val context: Context) {

    private val dataStore = context.settingsDataStore

    // ------------------------
    // 1) DARK MODE (Boolean)
    // ------------------------
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.DARK_MODE] = enabled
        }
    }

    val darkModeFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.DARK_MODE] ?: false
    }

    // ------------------------
    // 2) USERNAME (String)
    // ------------------------
    suspend fun setUsername(name: String) {
        dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.USERNAME] = name
        }
    }

    val usernameFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[UserPreferencesKeys.USERNAME] ?: ""
    }
}