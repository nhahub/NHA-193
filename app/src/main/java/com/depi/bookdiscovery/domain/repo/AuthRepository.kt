package com.depi.bookdiscovery.domain.repo

import com.depi.bookdiscovery.domain.model.User
import com.depi.bookdiscovery.util.Result

/**
 * [AuthRepository]
 * This interface defines the **contract** for all user authentication and authorization
 * operations within the application.
 * * * Located in the Domain Layer, this contract is independent of the Data Layer's
 * implementation details (i.e., whether Firebase, a custom API, or local storage is used).
 * It dictates the business logic methods available to the Use Cases or ViewModels.
 */
interface AuthRepository {
    /**
     * Registers a new user with the provided credentials.
     * * @param name The user's desired display name.
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] wrapper containing the authenticated [User] domain model upon success.
     */
    suspend fun signUp(name: String, email: String, password: String): Result<User>

    /**
     * Authenticates an existing user with their email and password.
     * * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] wrapper containing the authenticated [User] domain model upon success.
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Authenticates a user using a third-party service's ID token (e.g., Google Sign-In).
     * * @param idToken The ID token received from the external authentication provider.
     * @return A [Result] wrapper containing the authenticated [User] domain model upon success.
     */
    suspend fun loginWithGoogle(idToken: String): Result<User>


    /**
     * Performs the necessary steps to terminate the user's current session (logout).
     */
    fun logout()

    /**
     * Retrieves the data of the currently logged-in user.
     * * The underlying implementation is responsible for determining the best source
     * (local cache or remote check) for this data.
     * * @return The currently logged-in [User] domain model, or null if no user is authenticated.
     */
    suspend fun getCurrentUser(): User?

    /**
     * Initiates the password reset process by sending a password reset link/email.
     * * @param email The email address associated with the account.
     * @return A [Result] indicating success or failure of the request. The Unit type means no data is expected on success.
     * Still under development
     */
    suspend fun sendPasswordReset(email: String): Result<Unit>
}
