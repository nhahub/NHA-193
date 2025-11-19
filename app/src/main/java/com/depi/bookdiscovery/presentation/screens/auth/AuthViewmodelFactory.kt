package com.depi.bookdiscovery.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.depi.bookdiscovery.domain.usecase.*

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
        return when(modelClass) {

            LoginViewModel::class.java ->
                LoginViewModel(loginUseCase, googleLoginUseCase, sendResetEmailUseCase) as T

            SignUpViewModel::class.java ->
                SignUpViewModel(signUpUseCase,googleLoginUseCase) as T

            AuthViewModel::class.java ->
                AuthViewModel( getCurrentUserUseCase, logoutUseCase) as T

            else -> throw IllegalArgumentException("Unknown ViewModel: $modelClass")
        }
    }
}
