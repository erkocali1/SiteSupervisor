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
import com.muzo.sitesupervisor.domain.DeleteTaskUseCase
import com.muzo.sitesupervisor.domain.GetTeamUseCase
import com.muzo.sitesupervisor.domain.SaveTaskUseCase
import com.muzo.sitesupervisor.feature.fragment.detail.UpdateState
import com.muzo.sitesupervisor.feature.fragment.settingsFragment.team.GetTeamState
import com.muzo.sitesupervisor.feature.fragment.taskFragment.TeamTaskState
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
    private val getTeamUseCase: GetTeamUseCase,
    authRepository: AuthRepository,
    private val deleteTaskUseCase: DeleteTaskUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SaveTaskState> = MutableStateFlow(SaveTaskState())
    val uiState = _uiState

    private val _getTaskTeamState: MutableStateFlow<GetTeamTaskState> = MutableStateFlow(GetTeamTaskState())
    val getTaskTeamState = _getTaskTeamState

    private val _deleteTaskState: MutableStateFlow<DeleteTaskState> = MutableStateFlow(DeleteTaskState())
    val deleteTaskState = _deleteTaskState

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
                        _uiState.value = SaveTaskState(loading = false, isSaveTask = true)
                    }
                }

            }.launchIn(this)
        }
    }

    fun getTeam(currentUser: String, constructionName: String) {
        viewModelScope.launch {
            getTeamUseCase(currentUser, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _getTaskTeamState.value = _getTaskTeamState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _getTaskTeamState.value = _getTaskTeamState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _getTaskTeamState.value =
                            _getTaskTeamState.value.copy(loading = false, resultList = result.data)

                    }
                }
            }.launchIn(this)
        }
    }

    fun deleteTask(currentUser: String, constructionName: String, date: String, taskId: String) {
        viewModelScope.launch {
            deleteTaskUseCase(currentUser, constructionName, date, taskId).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _deleteTaskState.value = _deleteTaskState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _deleteTaskState.value = _deleteTaskState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _deleteTaskState.value =
                            _deleteTaskState.value.copy(loading = false,isDelete = true)

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

data class GetTeamTaskState(
    val resultList: List<String>? = null, val loading: Boolean = false
)

data class DeleteTaskState(
    val loading: Boolean = false,
    val isDelete: Boolean = false
)