package com.muzo.sitesupervisor.feature.fragment.photoFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.local.repository.LocalPostRepository
import com.muzo.sitesupervisor.domain.DeletePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoFragmentViewModel @Inject constructor(
    private val repository: LocalPostRepository,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val dataStore: MyDataStore,

    ) : ViewModel() {


    private val _uiState: MutableStateFlow<PhotoFragmentState> =
        MutableStateFlow(PhotoFragmentState())
    val uiState = _uiState


    fun deletePhotoFromFireBase(
        currentUser: String, constructionName: String, postId: String, photoUrlToDelete: String
    ) {
        viewModelScope.launch {
            deletePhotoUseCase(currentUser, constructionName, postId, photoUrlToDelete).asReSource()
                .onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _uiState.value = PhotoFragmentState(loading = false)
                        }

                        Resource.Loading -> {
                            _uiState.value = PhotoFragmentState(loading = true)
                        }

                        is Resource.Success -> {
                            _uiState.value = PhotoFragmentState(loading = false, result = true)
                        }

                    }
                }.launchIn(this)
        }
    }


    suspend fun deletePhotoUrl(postId: Long, urlToDelete: String) {
        repository.updatePost(postId, urlToDelete)
    }

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }


}

data class PhotoFragmentState(
    val loading: Boolean = false,
    val result: Boolean = false,

    )