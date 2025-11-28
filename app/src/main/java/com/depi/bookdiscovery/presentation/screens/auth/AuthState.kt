package com.depi.bookdiscovery.presentation.screens.auth


data class AuthFormState(
    val email: String = "",
    val password: String = "",
    val successUserId: String? = null,
    val isLoading: Boolean = false,
    val generalError: String? = null,
    val isSuccess: Boolean = false,

    val name: String = "",
    val confirmPassword: String = "",
    val termsAccepted: Boolean = false,

    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val confirmPasswordError: String? = null,
    val termsError: Boolean = false
)

data class SessionState(
    val isAuthenticated: Boolean = false,
    val currentUserId: String? = null,
    val currentUserEmail: String? = null,
    val currentUserName: String? = null,
    val isLoading: Boolean = true
)


