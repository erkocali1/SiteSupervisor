package com.muzo.sitesupervisor.feature.fragment.registerfragment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.UserInfo
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.AddUserInfoUseCase
import com.muzo.sitesupervisor.feature.fragment.detail.UpdateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterFragmentViewModel @Inject constructor(
    private val repository: AuthRepository,
    authRepository: AuthRepository,
    private val addUserInfoUseCase: AddUserInfoUseCase
) :
    ViewModel() {

    private val _signUpState = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: StateFlow<Resource<FirebaseUser>?> = _signUpState

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState())
    val uiState = _uiState


    val currentUser = authRepository.currentUser

    init {
        if (repository.currentUser != null) {
            _signUpState.value = Resource.Success(repository.currentUser!!)
        }
    }

    fun signUp(name: String, email: String, password: String) = viewModelScope.launch {
        _signUpState.value = Resource.Loading
        val result = repository.register(name, email, password)
        _signUpState.value = result

    }

    fun addUserInfo(currentUser:String, userInfo: UserInfo) {
        viewModelScope.launch {
            addUserInfoUseCase(currentUser, userInfo).asReSource().onEach { result ->

                when (result) {
                    Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(loading = false, result =true)
                    }
                }
            }.launchIn(this)

        }
    }
}
data class UIState(
    val loading: Boolean = false,
    val result: Boolean = false,

    )