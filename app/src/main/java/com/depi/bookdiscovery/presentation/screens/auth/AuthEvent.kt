package com.depi.bookdiscovery.presentation.screens.auth

sealed class AuthFormEvent {
    data class EmailChanged(val value: String) : AuthFormEvent()
    data class PasswordChanged(val value: String) : AuthFormEvent()
    data class NameChanged(val value: String) : AuthFormEvent()
    data class ConfirmPasswordChanged(val value: String) : AuthFormEvent()
    data class TermsChanged(val accepted: Boolean) : AuthFormEvent()

    object SubmitLogin : AuthFormEvent()
    object SubmitSignUp : AuthFormEvent()
    object ForgotPassword : AuthFormEvent()
    data class GoogleLogin(val idToken: String) : AuthFormEvent()

    object ClearError : AuthFormEvent()
}

