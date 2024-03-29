package com.muzo.sitesupervisor.feature.fragment.listingNotes

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants.Companion.OK_MESSAGE
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.GetAllPostUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ListingFramentViewModel @Inject constructor(
    private val getAllPostUseCase: GetAllPostUseCase,
    private val authRepository: AuthRepository,
    private val dataStore: MyDataStore,
) : ViewModel() {

    private val _uiState: MutableStateFlow<GetDataState> = MutableStateFlow(GetDataState())
    val uiState = _uiState
    val currentUser = authRepository.currentUser?.uid.toString()


    fun getAllData(currentUser: String, constructionName: String) {

        viewModelScope.launch {
            getAllPostUseCase(currentUser, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false, message = result.exception?.message
                        )
                    }

                    Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            message = OK_MESSAGE,
                            isSuccessful = true,
                            resultList = result.data
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }


}

data class GetDataState(
    val loading: Boolean = false,
    val message: String? = null,
    val isSuccessful: Boolean = false,
    val resultList: List<DataModel>? = null,
    val uriList: Uri? = null,
    val idsList: List<String>? = null
)