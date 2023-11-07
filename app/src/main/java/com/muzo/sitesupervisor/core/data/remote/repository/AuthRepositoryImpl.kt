package com.muzo.sitesupervisor.core.data.remote.repository

import com.google.firebase.auth.FirebaseUser
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.data.remote.source.AuthDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = authDataSource.currentUser

    override suspend fun register(
        name: String, email: String, password: String
    ): Resource<FirebaseUser> {
        return authDataSource.register(name = name, email = email, password = password)
    }

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return authDataSource.login(email, password)
    }

    override fun logOut() {
        authDataSource.logOut()

    }
}