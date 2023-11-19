package com.muzo.sitesupervisor.feature.fragment.detailFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants.Companion.ERROR_MESSAGE
import com.muzo.sitesupervisor.core.constans.Constants.Companion.OK_MESSAGE
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.UpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class DetailFragmentViewModel @Inject constructor(
    private val useCase: UpdateUseCase, authRepository: AuthRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<UpdateState> = MutableStateFlow(UpdateState())
    val uiState = _uiState

    val currentUser = authRepository.currentUser.toString()


    fun updateData(dataModel: DataModel) {


        viewModelScope.launch {
            useCase(dataModel).asReSource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _uiState.value =
                            _uiState.value.copy(loading = false, message = ERROR_MESSAGE)
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(loading = false, message = OK_MESSAGE)
                    }
                }
            }
        }
    }

    fun getCurrentDateAndTime(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)
        val currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
        return Pair(currentDate, currentTime)
    }
}

data class UpdateState(
    val loading: Boolean = false,
    val message: String? = null,
    val isSuccessful: Boolean = false,
)
