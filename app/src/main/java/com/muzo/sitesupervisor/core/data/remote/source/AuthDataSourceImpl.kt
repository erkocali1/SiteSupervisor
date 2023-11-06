package com.muzo.sitesupervisor.core.data.remote.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.await
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    AuthDataSource {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun logOut() {
        firebaseAuth.signOut()
    }
}