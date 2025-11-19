package com.depi.bookdiscovery.data.remote

import com.depi.bookdiscovery.domain.model.User
import com.depi.bookdiscovery.util.Result

interface UserRemoteDataSource {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<User>
//    suspend fun getUserProfile(): Result<User>
    fun logout()
    fun getCurrentUser(): User?
    suspend fun sendPasswordReset(email: String): Result<Unit>
}