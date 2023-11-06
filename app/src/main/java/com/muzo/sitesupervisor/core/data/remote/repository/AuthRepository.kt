package com.muzo.sitesupervisor.core.data.remote.repository

import com.google.firebase.auth.FirebaseUser
import com.muzo.sitesupervisor.core.common.Resource

interface AuthRepository {

    suspend fun register(name:String,email:String,password:String):Resource<FirebaseUser>

    suspend fun login(email: String,password: String):Resource<FirebaseUser>

    fun logOut()

}