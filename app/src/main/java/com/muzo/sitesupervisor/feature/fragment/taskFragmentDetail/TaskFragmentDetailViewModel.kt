package com.muzo.sitesupervisor.feature.fragment.taskFragmentDetail

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.local.repository.LocalPostRepository
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.SaveTaskUseCase
import com.muzo.sitesupervisor.feature.fragment.detail.UpdateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaskFragmentDetailViewModel @Inject constructor(
    private val localPostRepository: LocalPostRepository,
    private val saveTaskUseCase: SaveTaskUseCase,
    private val dataStore: MyDataStore,
    authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SaveTaskState> = MutableStateFlow(SaveTaskState())
    val uiState = _uiState


    val currentUser = authRepository.currentUser?.uid.toString()

    suspend fun saveRoom(taskList: TaskModel): Long {
        return localPostRepository.saveTask(taskList)
    }

    fun saveTask(taskModel: TaskModel) {
        viewModelScope.launch {
            saveTaskUseCase(taskModel).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _uiState.value = SaveTaskState(loading = false)
                    }

                    Resource.Loading -> {
                        _uiState.value = SaveTaskState(loading = true)
                    }

                    is Resource.Success -> {
                        _uiState.value=SaveTaskState(loading = false, isSaveTask = true)
                    }
                }

            }.launchIn(this)
        }
    }

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }
}

data class SaveTaskState(
    val loading: Boolean = false,
    val message: String? = null,
    val isSaveTask: Boolean = false,
)