package com.muzo.sitesupervisor.feature.fragment.registerfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterFragmentViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {

    private val _signUpState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: StateFlow<Resource<FirebaseUser>?> = _signUpState

    init {
        if(repository.currentUser !=null){
            _signUpState.value=Resource.Success(repository.currentUser!!)
        }
    }

    fun signUp(name:String,email:String,password:String)=viewModelScope.launch {
        _signUpState.value=Resource.Loading
         val result=repository.register(name, email, password)
        _signUpState.value=result

    }


}