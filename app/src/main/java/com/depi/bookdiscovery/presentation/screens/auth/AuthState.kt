package com.depi.bookdiscovery.presentation.screens.auth


data class AuthState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val termsAccepted: Boolean = false,

    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val termsError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val generalError: String? = null
)


data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val successUserId: String? = null,
    val generalError: String? = null
)
