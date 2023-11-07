package com.muzo.sitesupervisor.core.data.remote.source

import com.google.firebase.auth.FirebaseUser
import com.muzo.sitesupervisor.core.common.Resource


interface AuthDataSource {

    val currentUser:FirebaseUser?

    suspend fun register(name:String,email:String,password:String):Resource<FirebaseUser>

    suspend fun login(email: String,password: String):Resource<FirebaseUser>

    fun logOut()
}