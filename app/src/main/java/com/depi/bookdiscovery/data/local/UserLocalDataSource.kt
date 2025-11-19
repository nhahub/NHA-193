package com.depi.bookdiscovery.data.local

import com.depi.bookdiscovery.data.model.UserEntity

interface UserLocalDataSource {
    suspend fun saveUser(user: UserEntity)
    suspend fun getUser(): UserEntity?
    suspend fun clearUser()
}
