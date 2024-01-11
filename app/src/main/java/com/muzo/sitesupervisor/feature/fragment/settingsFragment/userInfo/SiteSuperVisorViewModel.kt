package com.muzo.sitesupervisor.feature.fragment.settingsFragment.userInfo

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.AddUserImageUrlUseCase
import com.muzo.sitesupervisor.domain.AddUserImageUseCase
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
    private val addUserImageUrlUseCase: AddUserImageUrlUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _photoLoadState: MutableStateFlow<PhotoLoadState> = MutableStateFlow(PhotoLoadState())
    val photoLoadState = _photoLoadState

    private val _loadUrlState: MutableStateFlow<UrlLoadState> = MutableStateFlow(UrlLoadState())
    val loadUrlState = _loadUrlState

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

    fun addUrlToFireBase(downloadUrl: Uri, currentUser: String, constructionName: String) {
        viewModelScope.launch {
            addUserImageUrlUseCase(downloadUrl, currentUser, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _loadUrlState.value = _loadUrlState.value.copy(loading = false)
                    }

                    is Resource.Loading -> {
                        _loadUrlState.value = _loadUrlState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _loadUrlState.value = _loadUrlState.value.copy(loading = false,result = true)
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