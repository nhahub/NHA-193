package com.depi.bookdiscovery.domain.usecase

import com.depi.bookdiscovery.data.local.UserLocalDataSource
import com.depi.bookdiscovery.domain.repo.AuthRepository

/**
 * [SignUpUseCase]
 * A use case that encapsulates the business rule for signing up a new user.
 * * It delegates the actual data operation to the [AuthRepository].
 * * Using the 'operator fun invoke' allows this class instance to be called directly as a function.
 *
 * @param repo The repository responsible for authentication operations.
 */
class SignUpUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String) =
        repo.signUp(name, email, password)
}

/**
 * [LoginUseCase]
 * A use case that encapsulates the business rule for authenticating a user with credentials.
 * * It provides a clear separation between the presentation layer (ViewModel) and the repository.
 *
 * @param repo The repository responsible for authentication operations.
 */
class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repo.login(email, password)
}

/**
 * [GoogleLoginUseCase]
 * A use case dedicated to handling third-party sign-in logic (e.g., Google).
 * * It accepts the ID token and delegates the authentication process to the repository.
 *
 * @param repo The repository responsible for authentication operations.
 */
class GoogleLoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(idToken: String) = repo.loginWithGoogle(idToken)
}

/**
 * [LogoutUseCase]
 * A simple use case to execute the business rule of terminating the user's current session.
 * * This operation does not suspend as it typically involves clearing local preferences or tokens.
 *
 * @param repo The repository responsible for authentication operations.
 */
class LogoutUseCase(
    private val repository: AuthRepository,
    private val userLocalDataSource: UserLocalDataSource
) {
    suspend operator fun invoke() {
        repository.logout()  // remote logout
        userLocalDataSource.clearUser() // clear local (suspend)
    }
}

/**
 * [GetCurrentUserUseCase]
 * A use case to retrieve the data of the currently logged-in user.
 * * The repository is responsible for the strategy (local cache vs. remote check).
 *
 * @param repo The repository responsible for authentication operations.
 */
class GetCurrentUserUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.getCurrentUser()
}

/**
 * [SendResetEmailUseCase]
 * A use case that encapsulates the logic for initiating the password reset flow.
 *
 * @param repo The repository responsible for authentication operations.
 * STILL UNDER DEVELOPMENT
 */
class SendResetEmailUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String) = repo.sendPasswordReset(email)
}
