package com.depi.bookdiscovery.presentation.screens.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.depi.bookdiscovery.domain.usecase.GetCurrentUserUseCase
import com.depi.bookdiscovery.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val currentUserId: String? = null,
    val isLoading: Boolean = true ,
    val currentUserName: String? = null,
    val currentUserEmail: String? = null,

    val error: String? = null,
    val isLoggedOut: Boolean = false
)

class AuthViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
): ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())

    init {
        checkCurrentUser()
    }
    fun checkCurrentUser() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase() // User object should have name and email fields
            _state.value = _state.value.copy(
                isAuthenticated = user != null,
                currentUserId = user?.uid,
                currentUserName = user?.name,
                currentUserEmail = user?.email,
                isLoading = false
            )
        }
    }


    fun logout() {
        logoutUseCase()
        _state.value = AuthUiState(isAuthenticated = false)
    }
}
