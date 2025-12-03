package com.depi.bookdiscovery.presentation.screens.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.domain.usecase.GetCurrentUserUseCase
import com.depi.bookdiscovery.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the user session state.
 * It tracks whether the user is currently authenticated and provides logic for checking the user
 * and logging out.
 *
 * @param getCurrentUserUseCase Use case for fetching the current logged-in user details.
 * @param logoutUseCase Use case for executing the user logout operation.
 */

class AuthViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state.asStateFlow()

    init {
        checkCurrentUser()
    }

    /**
     * Checks if a user is currently logged in.
     * Updates the [SessionState] with user information or the unauthenticated status.
     */
    fun checkCurrentUser() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            _state.value = _state.value.copy(
                isAuthenticated = user != null,
                currentUserId = user?.uid,
                currentUserName = user?.name,
                currentUserEmail = user?.email,
                isLoading = false
            )
        }
    }

    /**
     * Executes the logout operation and resets the session state.
     */
    fun logout() {
        viewModelScope.launch {
            logoutUseCase()

            _state.value = SessionState(isAuthenticated = false, isLoading = false)
        }
    }
}
