package com.depi.bookdiscovery.data.repo

import com.depi.bookdiscovery.data.local.UserLocalDataSource
import com.depi.bookdiscovery.data.model.UserMapper
import com.depi.bookdiscovery.data.remote.UserRemoteDataSource
import com.depi.bookdiscovery.domain.model.User
import com.depi.bookdiscovery.domain.repo.AuthRepository
import com.depi.bookdiscovery.util.Result

class AuthRepositoryImpl(
    private val ds: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource
     ): AuthRepository {

    override suspend fun signUp(name: String, email: String, password: String): Result<User> {
       val result = ds.register(name, email, password)

        if (result is Result.Success){
            val userEntity = UserMapper.domainToEntity(result.data)

            localDataSource.saveUser(userEntity)

        }
        return result
    }

    override suspend fun login(email: String, password: String): Result<User> {
        val result = ds.login(email, password)

        if (result is Result.Success) {
            val userEntity = UserMapper.domainToEntity(result.data)

            localDataSource.saveUser(userEntity)
        }
        return result
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> =
        ds.loginWithGoogle(idToken)

    override fun logout() = ds.logout()

    override suspend fun getCurrentUser(): User? {
        val localUser = localDataSource.getUser()
        if (localUser != null) {
            return UserMapper.entityToDomain(localUser)
        }
        return ds.getCurrentUser()
    }

    override suspend fun sendPasswordReset(email: String) = ds.sendPasswordReset(email)
}
