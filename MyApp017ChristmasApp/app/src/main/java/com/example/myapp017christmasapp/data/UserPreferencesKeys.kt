package com.example.myapp017christmasapp.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {
    // Zapnutí / vypnutí Dark Mode
    val DARK_MODE = booleanPreferencesKey("dark_mode")

    // Uložené jméno uživatele (pro podpis v dopise Ježíškovi)
    val USERNAME = stringPreferencesKey("username")
}