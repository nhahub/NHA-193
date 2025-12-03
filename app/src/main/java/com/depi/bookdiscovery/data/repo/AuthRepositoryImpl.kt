package com.depi.bookdiscovery.data.repo

import com.depi.bookdiscovery.data.local.UserLocalDataSource
import com.depi.bookdiscovery.data.model.UserMapper
import com.depi.bookdiscovery.data.remote.UserRemoteDataSource
import com.depi.bookdiscovery.domain.model.User
import com.depi.bookdiscovery.domain.repo.AuthRepository
import com.depi.bookdiscovery.util.Result

class AuthRepositoryImpl(
    private val ds: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource
     ): AuthRepository {

    /**
     * Attempts to sign up a new user via the remote service.
     * * Upon a successful remote registration:
     * 1. The Domain Model is mapped back to an Entity Model using [UserMapper.domainToEntity].
     * 2. The user's details are persisted locally using [localDataSource.saveUser()] to establish a session.
     * * @return A [Result] wrapper containing the [User] domain model upon success, or an error.
     */
    override suspend fun signUp(name: String, email: String, password: String): Result<User> {
       val result = ds.register(name, email, password)

        if (result is Result.Success){
            val userEntity = UserMapper.domainToEntity(result.data)

            localDataSource.saveUser(userEntity)

        }
        return result
    }

    /**
     * Attempts to log in an existing user via the remote service.
     * * Upon a successful remote login:
     * 1. The Domain Model is mapped back to an Entity Model using [UserMapper.domainToEntity].
     * 2. The user's details are persisted locally using [localDataSource.saveUser()] to establish a session.
     * * @return A [Result] wrapper containing the [User] domain model upon success, or an error.
     */
    override suspend fun login(email: String, password: String): Result<User> {
        val result = ds.login(email, password)

        if (result is Result.Success) {
            val userEntity = UserMapper.domainToEntity(result.data)

            localDataSource.saveUser(userEntity)
        }
        return result
    }

    /**
     * Executes the Google Sign-In process using the provided ID token directly via the remote source.
     */
    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        val result = ds.loginWithGoogle(idToken)
        if (result is Result.Success) {
            val user = UserMapper.domainToEntity(result.data)
            localDataSource.saveUser(user)
        }
        return result
    }
    /**
     * Handles the user logout process.
     * Note: The clearing of the local session is assumed to happen within the Remote Data Source's logout implementation.
     */
    override fun logout() {
        ds.logout()
    }


    /**
     * Retrieves the current logged-in user.
     * * **Data Strategy (Source of Truth):**
     * 1. First, attempts to retrieve the user details from the **local storage**.
     * 2. If no local user is found, it attempts to get the user from the **remote source**.
     * * The retrieved Entity is mapped to the Domain Model before being returned.
     * * @return The currently logged-in [User] domain model, or null if no user is found locally or remotely.
     */
    override suspend fun getCurrentUser(): User? {
        val localUser = localDataSource.getUser()

        // 1. Check local storage first (faster and defines session state)
        if (localUser != null) {
            return UserMapper.entityToDomain(localUser)
        }
        // 2. Fallback to remote source
        return ds.getCurrentUser()
    }

    /**
     * Sends a password reset email to the specified email address via the remote authentication service.
     * STILL UNDER DEVELOPMENT
     */
    override suspend fun sendPasswordReset(email: String) = ds.sendPasswordReset(email)
}
