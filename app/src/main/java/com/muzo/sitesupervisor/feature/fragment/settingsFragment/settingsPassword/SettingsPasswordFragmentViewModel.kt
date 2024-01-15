package com.muzo.sitesupervisor.feature.fragment.settingsFragment.settingsPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.domain.ChangePasswordUseCase
import com.muzo.sitesupervisor.domain.GetPasswordUseCase
import com.muzo.sitesupervisor.feature.fragment.verifyPasswordFragment.GetPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsPasswordFragmentViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val dataStore: MyDataStore,
    private val getPasswordUseCase: GetPasswordUseCase

    ) : ViewModel() {

    private val _changePasswordState: MutableStateFlow<ChangePasswordLoadState> = MutableStateFlow(ChangePasswordLoadState())
    val changePasswordState = _changePasswordState

    private val _passwordState: MutableStateFlow<PasswordState> = MutableStateFlow(PasswordState())
    val getPasswordState = _passwordState


    fun changePassword(itemValue: String, siteSuperVisor: String, constructionName: String) {
        viewModelScope.launch {
            changePasswordUseCase(itemValue, siteSuperVisor, constructionName).asReSource()
                .onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _changePasswordState.value =
                                _changePasswordState.value.copy(loading = false)
                        }

                        is Resource.Loading -> {
                            _changePasswordState.value =
                                _changePasswordState.value.copy(loading = true)
                        }

                        is Resource.Success -> {
                            _changePasswordState.value =
                                _changePasswordState.value.copy(loading = false, result = true)
                        }
                    }
                }.launchIn(this)
        }
    }

    fun getPassword(siteSuperVisor: String, constructionName: String) {
        viewModelScope.launch {
            getPasswordUseCase(siteSuperVisor, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _passwordState.value = _passwordState.value.copy(loading = false)
                    }

                    is Resource.Loading -> {
                        _passwordState.value = _passwordState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _passwordState.value =
                            _passwordState.value.copy(loading = false, password = result.data)
                    }
                }
            }.launchIn(this)
        }
    }

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }

}

data class ChangePasswordLoadState(
    val loading: Boolean = false,
    val result: Boolean = false,
)
data class PasswordState(
    val loading: Boolean = false,
    val password: String? = null
)