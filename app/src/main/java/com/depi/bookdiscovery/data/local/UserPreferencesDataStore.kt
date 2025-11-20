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


val Context.userDataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesDataStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    }

    suspend fun saveUser(user: UserEntity) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = user.id
            prefs[KEY_USER_NAME] = user.name
            prefs[KEY_USER_EMAIL] = user.email
        }
    }

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
    val isUserLoggedInFlow: Flow<Boolean> = dataStore.data
        .map { prefs ->
            prefs[KEY_USER_ID] != null
        }

    suspend fun clearUser() {
        dataStore.edit { it.clear() }

    }
}
