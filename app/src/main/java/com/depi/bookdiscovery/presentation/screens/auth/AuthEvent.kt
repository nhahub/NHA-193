package com.depi.bookdiscovery.presentation.screens.auth


sealed class AuthEvent {
    data class NameChanged(val value: String): AuthEvent()
    data class EmailChanged(val value: String): AuthEvent()
    data class PasswordChanged(val value: String): AuthEvent()
    data class ConfirmPasswordChanged(val value: String): AuthEvent()
    data class TermsChanged(val accepted: Boolean): AuthEvent()

    object SubmitSignUp: AuthEvent()
    object ClearError : AuthEvent()
}

sealed class LoginEvent {
    data class EmailChanged(val v: String): LoginEvent()
    data class PasswordChanged(val v: String): LoginEvent()
    object Submit: LoginEvent()
    object ForgotPassword: LoginEvent()
}
