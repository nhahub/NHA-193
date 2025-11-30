package com.depi.bookdiscovery.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.depi.bookdiscovery.domain.usecase.*

/**
 * [AuthViewModelFactory]
 * A custom factory implementation for creating instances of authentication-related ViewModels.
 * This factory handles the dependency injection for the required Use Cases.
 *
 * @param signUpUseCase The use case for user registration.
 * @param loginUseCase The use case for standard email/password login.
 * @param googleLoginUseCase The use case for third-party sign-in (e.g., Google).
 * @param logoutUseCase The use case for terminating the user session.
 * @param getCurrentUserUseCase The use case for retrieving the details of the currently authenticated user.
 * @param sendResetEmailUseCase The use case for initiating the password reset process.
 */
class AuthViewModelFactory(
    private val signUpUseCase: SignUpUseCase,
    private val loginUseCase: LoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sendResetEmailUseCase: SendResetEmailUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthFormViewModel::class.java) -> {
                AuthFormViewModel(
                    loginUseCase,
                    signUpUseCase,
                    googleLoginUseCase,
                    sendResetEmailUseCase
                ) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    getCurrentUserUseCase,
                    logoutUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
