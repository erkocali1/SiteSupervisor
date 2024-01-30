package com.muzo.sitesupervisor.feature.fragment.userFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.model.UserInfo
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.GetSiteSuperVisorInfoUseCase
import com.muzo.sitesupervisor.feature.fragment.settingsFragment.userInfo.GetInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class UserFragmentViewModel @Inject constructor(private val authRepository: AuthRepository,
    private val getSiteSuperVisorInfoUseCase: GetSiteSuperVisorInfoUseCase):ViewModel() {

    private val _infoState: MutableStateFlow<InfoState> = MutableStateFlow(InfoState())
    val infoState = _infoState

    fun logOut() {
        authRepository.logOut()
    }

    val currentUser = authRepository.currentUser?.uid.toString()

    fun getSiteSuperVisorInfo(siteSuperVisor: String) {
        viewModelScope.launch {
            getSiteSuperVisorInfoUseCase(siteSuperVisor).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _infoState.value = _infoState.value.copy(loading = false)
                    }

                    is Resource.Loading -> {
                        _infoState.value = _infoState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _infoState.value = _infoState.value.copy(loading = false, userInfoList = result.data)
                    }
                }
            }.launchIn(this)
        }
    }

}

data class InfoState(
    val loading: Boolean = false,
    val userInfoList: UserInfo? = null
)