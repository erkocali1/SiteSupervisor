package com.muzo.sitesupervisor.feature.fragment.loginfragment

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

class LoginFragmentViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {

    private val _loginState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginState

    init {
        if (repository.currentUser != null) {
            _loginState.value = Resource.Success(repository.currentUser!!)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginState.value = Resource.Loading
        val result = repository.login(email, password)
        _loginState.value = result
    }

}