package com.depi.bookdiscovery.presentation.screens.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.domain.usecase.LoginUseCase
import com.depi.bookdiscovery.domain.usecase.SendResetEmailUseCase
import com.depi.bookdiscovery.domain.usecase.GoogleLoginUseCase
import com.depi.bookdiscovery.util.Result
import com.depi.bookdiscovery.util.Validation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val sendResetEmailUseCase: SendResetEmailUseCase
): ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun onEvent(e: LoginEvent) {
        when(e) {
            is LoginEvent.EmailChanged -> _state.value = _state.value.copy(email = e.v, emailError = null)
            is LoginEvent.PasswordChanged -> _state.value = _state.value.copy(password = e.v, passwordError = null)
            LoginEvent.Submit -> submit()
            LoginEvent.ForgotPassword -> forgot()
        }
    }

    private fun submit() {
        val s = _state.value
        var hasError = false
        if (!Validation.isEmailValid(s.email)) { hasError = true; _state.value = s.copy(emailError = "Invalid email") }
        if (s.password.isBlank()) { hasError = true; _state.value = _state.value.copy(passwordError = "Enter password") }
        if (hasError) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)
            when(val r = loginUseCase(s.email, s.password)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, successUserId = r.data.uid)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, generalError = r.message)
                else -> _state.value = _state.value.copy(isLoading = false, generalError = "Unknown")
            }
        }
    }

    private fun forgot() {
        val email = _state.value.email
        if (!Validation.isEmailValid(email)) {
            _state.value = _state.value.copy(emailError = "Enter your email to reset")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when(val r = sendResetEmailUseCase(email)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, generalError = "Reset email sent")
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, generalError = r.message)
                else -> _state.value = _state.value.copy(isLoading = false, generalError = "Unknown")
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
                        successUserId = result.data.uid
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


}

