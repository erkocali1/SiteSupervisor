package com.muzo.sitesupervisor.feature.fragment.createAreaFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.domain.FireBaseSaveDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateAreFragmentViewModel @Inject constructor(private val fireBaseSaveDataUseCase: FireBaseSaveDataUseCase) :
    ViewModel() {

    val _uiState: MutableStateFlow<SaveDataState> = MutableStateFlow(SaveDataState())
    val uiState = _uiState


    fun save(data: DataModel) {
        viewModelScope.launch {
            fireBaseSaveDataUseCase(data).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _uiState.value =
                            _uiState.value.copy(loading = false)
                    }
                }
            }.launchIn(this)
        }


    }


}

data class SaveDataState(
    val loading: Boolean = false,
)