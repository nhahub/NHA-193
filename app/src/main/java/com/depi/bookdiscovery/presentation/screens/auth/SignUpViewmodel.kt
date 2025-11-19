package com.depi.bookdiscovery.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.domain.usecase.GoogleLoginUseCase
import com.depi.bookdiscovery.domain.usecase.SignUpUseCase
import com.depi.bookdiscovery.util.Result
import com.depi.bookdiscovery.util.Validation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SignUpViewModel(
    private val signUpUseCase: SignUpUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun onEvent(event: AuthEvent) {
        when(event) {

            is AuthEvent.NameChanged -> {
                _state.value = _state.value.copy(name = event.value, nameError = null, generalError = null)
            }

            is AuthEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.value, emailError = null, generalError = null)
            }

            is AuthEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.value, passwordError = null, generalError = null)
            }

            is AuthEvent.ConfirmPasswordChanged -> {
                _state.value = _state.value.copy(confirmPassword = event.value, confirmPasswordError = null, generalError = null)
            }

            is AuthEvent.TermsChanged -> {
                _state.value = _state.value.copy(termsAccepted = event.accepted, termsError = null, generalError = null)
            }

            AuthEvent.SubmitSignUp -> submit()
            AuthEvent.ClearError -> clearError()
        }
    }

    private fun submit() {
        val s = _state.value
        var hasError = false
        if (s.name.isBlank()) { hasError = true; _state.value = s.copy(nameError = "Name required") }
        if (!Validation.isEmailValid(s.email)) { hasError = true; _state.value = _state.value.copy(emailError = "Invalid email") }
        if (!Validation.isPasswordValid(s.password)) { hasError = true; _state.value = _state.value.copy(passwordError = "Min 6 chars") }
        if (!Validation.doPasswordsMatch(s.password, s.confirmPassword)) { hasError = true; _state.value = _state.value.copy(confirmPasswordError = "Passwords do not match") }
        if (!s.termsAccepted) { hasError = true; _state.value = _state.value.copy(termsError = "Accept terms") }

        if (hasError) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)
            when(val r = signUpUseCase(s.name, s.email, s.password)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, isSuccess = true,)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, generalError = r.message)
                else -> _state.value = _state.value.copy(isLoading = false, generalError = "Unknown Error Occurred")
            }
        }

    }


    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)

            when (val result = googleLoginUseCase(idToken)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        generalError = result.message
                    )
                }
                else -> {
                    _state.value = _state.value.copy(isLoading = false, generalError = "Unknown error")
                }
            }
        }
    }
    private fun clearError() {
        _state.value = _state.value.copy(generalError = null)
    }
}

