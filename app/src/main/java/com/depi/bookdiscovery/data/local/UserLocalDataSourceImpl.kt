package com.depi.bookdiscovery.data.local

import com.depi.bookdiscovery.data.model.UserEntity

class UserLocalDataSourceImpl(
    private val userPreferences: UserPreferencesDataStore
) : UserLocalDataSource {

    override suspend fun saveUser(user: UserEntity) {
        userPreferences.saveUser(user)
    }

    override suspend fun getUser(): UserEntity? {
        return userPreferences.getUser()
    }

    override suspend fun clearUser() {
        userPreferences.clearUser()
    }
}
