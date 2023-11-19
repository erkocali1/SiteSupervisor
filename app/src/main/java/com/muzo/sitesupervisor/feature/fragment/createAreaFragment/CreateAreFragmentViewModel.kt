package com.muzo.sitesupervisor.feature.fragment.createAreaFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants.Companion.OK_MESSAGE
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.FireBaseSaveDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class CreateAreFragmentViewModel @Inject constructor(
    private val fireBaseSaveDataUseCase: FireBaseSaveDataUseCase,
    authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SaveDataState> = MutableStateFlow(SaveDataState())
    val uiState = _uiState


    val currentUser = authRepository.currentUser


    fun saveArea(dataModel: DataModel) {
        viewModelScope.launch {
            fireBaseSaveDataUseCase(dataModel).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false, message = result.exception?.message
                        )
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false, message = OK_MESSAGE, isSuccessful = true
                        )

                    }
                }
            }.launchIn(this)
        }


    }




}

data class SaveDataState(
    val loading: Boolean = false, val message: String? = null, val isSuccessful: Boolean = false
)