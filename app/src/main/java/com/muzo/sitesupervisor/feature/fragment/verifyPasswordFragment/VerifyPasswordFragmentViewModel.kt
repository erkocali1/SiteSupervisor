package com.muzo.sitesupervisor.feature.fragment.verifyPasswordFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.UserInfo
import com.muzo.sitesupervisor.domain.GetPasswordUseCase
import com.muzo.sitesupervisor.domain.GetSiteSuperVisorInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyPasswordFragmentViewModel @Inject constructor(
    private val getPasswordUseCase: GetPasswordUseCase,
    private val dataStore: MyDataStore
) : ViewModel() {

    private val _getPasswordState: MutableStateFlow<GetPasswordState> = MutableStateFlow(GetPasswordState())
    val getPasswordState = _getPasswordState


    fun getPassword(siteSuperVisor: String, constructionName: String) {
        viewModelScope.launch {
            getPasswordUseCase(siteSuperVisor, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        getPasswordState.value = getPasswordState.value.copy(loading = false)
                    }

                    is Resource.Loading -> {
                        getPasswordState.value = getPasswordState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        getPasswordState.value =
                            getPasswordState.value.copy(loading = false, password = result.data)
                    }
                }
            }.launchIn(this)
        }
    }

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }


}

data class GetPasswordState(
    val loading: Boolean = false,
    val password: String? = null
)