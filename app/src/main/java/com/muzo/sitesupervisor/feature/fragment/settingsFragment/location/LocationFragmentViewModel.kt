package com.muzo.sitesupervisor.feature.fragment.settingsFragment.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.GetLocationUseCase
import com.muzo.sitesupervisor.domain.SaveLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LocationFragmentViewModel @Inject constructor(
    private val saveLocationUseCase: SaveLocationUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val dataStore: MyDataStore,
    authRepository: AuthRepository
) : ViewModel() {


    private val _uiState: MutableStateFlow<LocationFragmentState> = MutableStateFlow(LocationFragmentState())
    val uiState = _uiState

    private val _upLoadState: MutableStateFlow<UpLoadLocationState> = MutableStateFlow(UpLoadLocationState())
    val upLoadState = _upLoadState
    val currentUser = authRepository.currentUser?.uid.toString()



    fun saveLocation(latLng: LatLng, currentUser: String, constructionName: String) {
        viewModelScope.launch {
            saveLocationUseCase(latLng, currentUser, constructionName).asReSource()
                .onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(loading = false)
                        }

                        Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(loading = true)
                        }

                        is Resource.Success -> {
                            _uiState.value = _uiState.value.copy(loading = false, result = true)
                        }
                    }

                }.launchIn(this)
        }

    }


    fun upLoad(currentUser: String, constructionName: String) {
        viewModelScope.launch {
            getLocationUseCase(currentUser, constructionName).asReSource()
                .onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _upLoadState.value = _upLoadState.value.copy(loading = false)
                        }

                        Resource.Loading -> {
                            _upLoadState.value = _upLoadState.value.copy(loading = true)
                        }

                        is Resource.Success -> {
                            _upLoadState.value =
                                _upLoadState.value.copy(loading = false, result = true,resultList =result.data)
                        }
                    }

                }.launchIn(this)
        }

    }

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }
}

data class LocationFragmentState(
    val loading: Boolean = false,
    val result: Boolean = false,

    )

data class UpLoadLocationState(
    val loading: Boolean = false,
    val result: Boolean = false,
    val resultList: Pair<String, String>? = null
)