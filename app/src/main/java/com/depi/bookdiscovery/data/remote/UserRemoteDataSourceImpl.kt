package com.depi.bookdiscovery.data.remote

import com.depi.bookdiscovery.domain.model.User
import com.depi.bookdiscovery.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
class UserRemoteDataSourceImpl(
    private val firebaseAuth: FirebaseAuth,
) : UserRemoteDataSource {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.Error("Firebase login failed")

            Result.Success(
                User(
                    uid = user.uid,
                    name = user.displayName,
                    email = user.email
                )
            )
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Login failed")
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.Error("Registration failed")

            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            ).await()

            Result.Success(
                User(
                    uid = user.uid,
                    name = name,
                    email = email
                )
            )
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Registration failed")
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.Error("Google login failed")

            Result.Success(
                User(
                    uid = user.uid,
                    name = user.displayName,
                    email = user.email
                )
            )
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Google login failed")
        }
    }

    override fun getCurrentUser(): User? {
        val u = firebaseAuth.currentUser ?: return null
        return User(uid = u.uid, name = u.displayName, email = u.email)
    }

    override fun logout() = firebaseAuth.signOut()

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Failed to send reset email")
        }
    }
}
