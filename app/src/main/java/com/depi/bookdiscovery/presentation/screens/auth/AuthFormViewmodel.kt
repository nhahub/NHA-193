package com.depi.bookdiscovery.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.domain.usecase.*
import com.depi.bookdiscovery.util.Result
import com.depi.bookdiscovery.util.Validation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the state and logic for the Login and Sign-up forms.
 * It injects and utilizes various authentication Use Cases.
 */
class AuthFormViewModel(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val sendResetEmailUseCase: SendResetEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthFormState())
    val state: StateFlow<AuthFormState> = _state.asStateFlow()

    /**
     * Handles all incoming events from the UI and updates the state accordingly,
     * or triggers an action (like login/signup).
     */
    fun onEvent(event: AuthFormEvent) {
        when (event) {
            is AuthFormEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.value, emailError = null, generalError = null)
            }
            is AuthFormEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.value, passwordError = null, generalError = null)
            }
            is AuthFormEvent.NameChanged -> {
                _state.value = _state.value.copy(name = event.value, nameError = null, generalError = null)
            }
            is AuthFormEvent.ConfirmPasswordChanged -> {
                _state.value = _state.value.copy(confirmPassword = event.value, confirmPasswordError = null, generalError = null)
            }
            is AuthFormEvent.TermsChanged -> {
                _state.value = _state.value.copy(termsAccepted = event.accepted, termsError = false, generalError = null)
            }

            // Actions
            AuthFormEvent.SubmitLogin -> performLogin()
            AuthFormEvent.SubmitSignUp -> performSignUp()
            AuthFormEvent.ForgotPassword -> performForgotPassword()
            is AuthFormEvent.GoogleLogin -> performGoogleLogin(event.idToken)
            AuthFormEvent.ClearError -> _state.value = _state.value.copy(generalError = null)
        }
    }

    /**
     * Performs local validation for login data (email/password format)
     * and triggers the asynchronous login process via [loginUseCase].
     */
    private fun performLogin() {
        val s = _state.value
        if (!Validation.isEmailValid(s.email)) {
            _state.value = s.copy(emailError = "Invalid email format")
            return
        }
        if (s.password.isBlank() || s.password.length < 6) {
            _state.value = s.copy(passwordError = "Min 6 chars required")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)
            handleAuthResult(loginUseCase(s.email, s.password))
        }
    }

    /**
     * Performs comprehensive local validation for sign-up data (name, email, password match, terms)
     * and triggers the asynchronous sign-up process via [signUpUseCase].
     */
    private fun performSignUp() {
        val s = _state.value
        var hasError = false
        var tempState = s.copy(nameError = null, emailError = null, passwordError = null, confirmPasswordError = null, termsError = false)

        if (s.name.isBlank()) {
            tempState = tempState.copy(nameError = "Name is required")
            hasError = true
        }
        if (!Validation.isEmailValid(s.email)) {
            tempState = tempState.copy(emailError = "Invalid email format")
            hasError = true
        }
        if (!Validation.isPasswordValid(s.password)) {
            tempState = tempState.copy(passwordError = "Min 6 chars required")
            hasError = true
        }
        if (!Validation.doPasswordsMatch(s.password, s.confirmPassword)) {
            tempState = tempState.copy(confirmPasswordError = "Passwords do not match")
            hasError = true
        }
        if (!s.termsAccepted) {
            tempState = tempState.copy(termsError = true)
            hasError = true
        }

        _state.value = tempState
        if (hasError) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)
            handleAuthResult(signUpUseCase(s.name, s.email, s.password))
        }
    }

    /**
     * Triggers the asynchronous Google login flow using the provided ID token.
     */
    private fun performGoogleLogin(idToken: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)
            handleAuthResult(googleLoginUseCase(idToken))
        }
    }

    /**
     * Validates the email and sends a password reset email via [sendResetEmailUseCase].
     * STILL UNDER DEVELOPMENT
     */
    private fun performForgotPassword() {
        val email = _state.value.email
        if (!Validation.isEmailValid(email)) {
            _state.value = _state.value.copy(emailError = "Please enter a valid email first")
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)
            when (val result = sendResetEmailUseCase(email)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, generalError = "Reset email sent successfully!") // يمكن استخدام حقل successMessage بدلاً من generalError للعرض
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, generalError = result.message)
                else -> _state.value = _state.value.copy(isLoading = false, generalError = "Unknown error")
            }
        }
    }

    /**
     * Standardized handler to process the result of any authentication Use Case ([Result] type).
     * Updates loading and success/error states based on the result.
     */
    private fun handleAuthResult(result: Result<*>) {
        when (result) {
            is Result.Success -> {
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            }
            is Result.Error -> {
                _state.value = _state.value.copy(isLoading = false, generalError = result.message)
            }
            else -> {
                _state.value = _state.value.copy(isLoading = false, generalError = "Unknown Error")
            }
        }
    }
}