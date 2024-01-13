package com.muzo.sitesupervisor.feature.fragment.paswordFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import com.muzo.sitesupervisor.domain.ChangePasswordUseCase
import com.muzo.sitesupervisor.domain.ChangeUserITemUseCase
import com.muzo.sitesupervisor.feature.fragment.settingsFragment.userInfo.UrlLoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordFragmentViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
    authRepository: AuthRepository,
    private val dataStore: MyDataStore
) : ViewModel() {

    private val _loadPasswordState: MutableStateFlow<PasswordLoadState> = MutableStateFlow(PasswordLoadState())
    val loadPasswordState = _loadPasswordState

    val currentUser = authRepository.currentUser


    fun changePassword(itemValue: String, siteSuperVisor: String,constructionName: String) {
        viewModelScope.launch {
            changePasswordUseCase(itemValue, siteSuperVisor, constructionName).asReSource()
                .onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _loadPasswordState.value = _loadPasswordState.value.copy(loading = false)
                        }

                        is Resource.Loading -> {
                            _loadPasswordState.value = _loadPasswordState.value.copy(loading = true)
                        }

                        is Resource.Success -> {
                            _loadPasswordState.value = _loadPasswordState.value.copy(loading = false, result = true)
                        }
                    }
                }.launchIn(this)
        }
    }
    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }


}

data class PasswordLoadState(
    val loading: Boolean = false,
    val result: Boolean = false,
)