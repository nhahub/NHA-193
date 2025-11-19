package com.depi.bookdiscovery.domain.usecase

import com.depi.bookdiscovery.domain.repo.AuthRepository

class SignUpUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String) =
        repo.signUp(name, email, password)
}
class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repo.login(email, password)
}
class GoogleLoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(idToken: String) = repo.loginWithGoogle(idToken)
}
class LogoutUseCase(private val repo: AuthRepository) {
    operator fun invoke() = repo.logout()
}
class GetCurrentUserUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.getCurrentUser()
}
class SendResetEmailUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String) = repo.sendPasswordReset(email)
}
