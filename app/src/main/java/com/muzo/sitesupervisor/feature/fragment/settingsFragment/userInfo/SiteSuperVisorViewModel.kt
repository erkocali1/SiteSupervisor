package com.muzo.sitesupervisor.feature.fragment.settingsFragment.userInfo

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.UserInfo
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.ChangeUserITemUseCase
import com.muzo.sitesupervisor.domain.AddUserImageUseCase
import com.muzo.sitesupervisor.domain.GetSiteSuperVisorInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SiteSuperVisorViewModel @Inject constructor(
    private val dataStore: MyDataStore,
    private val addUserImageUseCase: AddUserImageUseCase,
    private val changeUserITemUseCase: ChangeUserITemUseCase,
    private val authRepository: AuthRepository,
    private val getSiteSuperVisorInfoUseCase: GetSiteSuperVisorInfoUseCase
) : ViewModel() {

    private val _photoLoadState: MutableStateFlow<PhotoLoadState> = MutableStateFlow(PhotoLoadState())
    val photoLoadState = _photoLoadState

    private val _loadItemState: MutableStateFlow<UrlLoadState> = MutableStateFlow(UrlLoadState())
    val loadItemState = _loadItemState

    private val _getInfoState: MutableStateFlow<GetInfoState> = MutableStateFlow(GetInfoState())
    val getInfoState = _getInfoState

    val currentUser: FirebaseUser?
        get() = authRepository.currentUser

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }

    fun addUserPhoto(fileUri: Uri, siteSuperVisor: String) {
        viewModelScope.launch {
            addUserImageUseCase(fileUri, siteSuperVisor).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _photoLoadState.value = _photoLoadState.value.copy(loading = false)
                    }

                    is Resource.Loading -> {
                        _photoLoadState.value = _photoLoadState.value.copy(loading = true)
                    }

                    is Resource.Success -> { _photoLoadState.value = _photoLoadState.value.copy(
                        loading = false,
                        message = Constants.OK_MESSAGE,
                        resultUriList = result.data
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    fun addItemValueToFireBase(itemValue: String, currentUser: String, changedItem: String) {
        viewModelScope.launch {
            changeUserITemUseCase(itemValue, currentUser, changedItem).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _loadItemState.value = _loadItemState.value.copy(loading = false)
                    }

                    is Resource.Loading -> {
                        _loadItemState.value = _loadItemState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _loadItemState.value = _loadItemState.value.copy(loading = false,result = true)
                    }
                }
            }.launchIn(this)
        }
    }

    fun getSiteSuperVisorInfo(siteSuperVisor: String) {
        viewModelScope.launch {
            getSiteSuperVisorInfoUseCase(siteSuperVisor).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _getInfoState.value = _getInfoState.value.copy(loading = false)
                    }

                    is Resource.Loading -> {
                        _getInfoState.value = _getInfoState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _getInfoState.value = _getInfoState.value.copy(loading = false, userInfoList = result.data)
                    }
                }
            }.launchIn(this)
        }
    }



}

data class PhotoLoadState(
    val loading: Boolean = false,
    val message: String? = null,
    val resultUriList: Uri? = null,
    val result: Boolean = false,
    val photoList: List<String>? = null,
    )

data class UrlLoadState(
    val loading: Boolean = false,
    val result: Boolean = false,
)

data class GetInfoState(
    val loading: Boolean = false,
    val userInfoList:UserInfo? = null
)