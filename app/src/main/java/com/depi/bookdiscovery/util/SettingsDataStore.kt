package com.depi.bookdiscovery.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val SEARCH_HISTORY = stringSetPreferencesKey("search_history")
    }

    val darkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: false
    }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = isDarkMode
        }
    }

    val language: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "en"
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    val searchHistory: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[SEARCH_HISTORY] ?: emptySet()
    }

    suspend fun addToSearchHistory(query: String) {
        dataStore.edit { preferences ->
            val currentHistory = preferences[SEARCH_HISTORY] ?: emptySet()
            val newHistory = mutableListOf<String>()
            newHistory.add(query)
            newHistory.addAll(currentHistory.filter { it != query })
            if (newHistory.size > 5) {
                newHistory.removeAt(newHistory.lastIndex)
            }
            preferences[SEARCH_HISTORY] = newHistory.toSet()
        }
    }

    suspend fun clearSearchHistory() {
        dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY)
        }
    }
}