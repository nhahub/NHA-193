package com.depi.bookdiscovery.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.depi.bookdiscovery.data.model.UserEntity
import kotlinx.coroutines.flow.first
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [DataStore Delegate]
 * Creates and initializes the DataStore instance at the application level.
 * The preferences file will be stored under the name "user_prefs".
 */
val Context.userDataStore by preferencesDataStore(name = "user_prefs")

/**
 * [UserPreferencesDataStore]
 * This class is responsible for reading, writing, and clearing user data (`UserEntity`)
 * safely and asynchronously using Jetpack DataStore.
 *
 * It acts as the concrete implementation for storing user-specific key-value pairs locally.
 */
class UserPreferencesDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }
    /**
     * Saves the complete user data (ID, Name, Email) into DataStore.
     *
     * @param user The UserEntity object to be stored.
     */

    suspend fun saveUser(user: UserEntity) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = user.id
            prefs[KEY_USER_NAME] = user.name
            prefs[KEY_USER_EMAIL] = user.email
        }
    }
    /**
     * Retrieves the stored user data from DataStore.
     * Uses the [.first()] terminal operator to get a single, non-continuous value.
     *
     * @return [UserEntity] if the user ID exists, otherwise returns null if the ID is missing.
     */

    suspend fun getUser(): UserEntity? {
        val prefs = dataStore.data.first()

        val id = prefs[KEY_USER_ID] ?: return null
        val name = prefs[KEY_USER_NAME] ?: ""
        val email = prefs[KEY_USER_EMAIL] ?: ""

        return UserEntity(
            id = id,
            name = name,
            email = email
        )
    }
    /**
     * [Flow of Login Status]
     * Provides a continuous stream (Flow) to observe the user's login state reactively.
     *
     * @return [Flow<Boolean>] true if the user ID key is present, false otherwise.
     */
    val isUserLoggedInFlow: Flow<Boolean> = dataStore.data
        .map { prefs ->
            prefs[KEY_USER_ID] != null
        }
    /**
     * Clears all stored user data and preferences from the DataStore.
     *
     * This is typically invoked during the user logout process.
     */
    suspend fun clearUser() {
        dataStore.edit { it.clear() }

    }
}
