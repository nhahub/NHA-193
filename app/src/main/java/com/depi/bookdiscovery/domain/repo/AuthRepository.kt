package com.depi.bookdiscovery.domain.repo

import com.depi.bookdiscovery.domain.model.User
import com.depi.bookdiscovery.util.Result

interface AuthRepository {
    suspend fun signUp(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<User>
    fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun sendPasswordReset(email: String): Result<Unit>
}
