package com.depi.bookdiscovery.presentation.screens.auth

/**
 * [AuthFormState]
 * Data class representing the mutable state of the authentication forms (Login/SignUp).
 * It holds all input values, validation errors, and operational states.
 */
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

/**
 * [SessionState]
 * Data class representing the overall session status of the application.
 * This is typically observed to determine navigation between Auth screens and Main screens.
 */
data class SessionState(
    val isAuthenticated: Boolean = false,
    val currentUserId: String? = null,
    val currentUserEmail: String? = null,
    val currentUserName: String? = null,
    val isLoading: Boolean = true
)


