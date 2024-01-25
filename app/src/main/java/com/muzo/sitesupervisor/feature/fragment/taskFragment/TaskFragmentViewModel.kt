package com.muzo.sitesupervisor.feature.fragment.taskFragment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.GetAllTaskUseCase
import com.muzo.sitesupervisor.domain.GetTaskDateUseCase
import com.muzo.sitesupervisor.domain.GetTasksWithWorkerUseCase
import com.muzo.sitesupervisor.domain.GetTeamUseCase
import com.muzo.sitesupervisor.feature.fragment.detail.UpdateState
import com.muzo.sitesupervisor.feature.fragment.taskFragmentDetail.GetTeamTaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskFragmentViewModel @Inject constructor(
    private val getAllTaskUseCase: GetAllTaskUseCase,
    private val getTaskDateUseCase: GetTaskDateUseCase,
    private val getTasksWithWorkerUseCase: GetTasksWithWorkerUseCase,
    private val getTeamUseCase: GetTeamUseCase,
    private val dataStore: MyDataStore,
    authRepository: AuthRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<TaskState> = MutableStateFlow(TaskState())
    val uiState = _uiState

    private val _dateState: MutableStateFlow<TaskDateState> = MutableStateFlow(TaskDateState())
    val dateState = _dateState

    private val _workerState: MutableStateFlow<WorkerState> = MutableStateFlow(WorkerState())
    val workerState = _workerState

    private val _teamTaskState: MutableStateFlow<TeamTaskState> = MutableStateFlow(TeamTaskState())
    val teamTaskState = _teamTaskState

    val currentUser = authRepository.currentUser?.uid.toString()


    fun getAllTask(currentUser: String, constructionName: String, date: String) {
        viewModelScope.launch {
            getAllTaskUseCase(currentUser, constructionName, date).asReSource().onEach { result ->

                when (result) {
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(loading = false)
                    }

                    Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _uiState.value =
                            _uiState.value.copy(loading = false, resultList = result.data)
                    }
                }

            }.launchIn(this)
        }
    }

    fun getTaskDate(currentUser: String, constructionName: String) {
        viewModelScope.launch {
            getTaskDateUseCase(currentUser, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _dateState.value = _dateState.value.copy(loading = false)
                    }

                    Resource.Loading -> {
                        _dateState.value = _dateState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _dateState.value =
                            _dateState.value.copy(loading = false, dateList = result.data)
                    }
                }
            }.launchIn(this)
        }
    }

    fun getWorker(workerName: String) {
        viewModelScope.launch {
            getTasksWithWorkerUseCase(workerName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _workerState.value = _workerState.value.copy(loading = false)
                    }

                    Resource.Loading -> {
                        _workerState.value = _workerState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _workerState.value = _workerState.value.copy(
                            loading = false, resulListWithWorker = result.data
                        )
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
                        _teamTaskState.value = _teamTaskState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _teamTaskState.value = _teamTaskState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _teamTaskState.value =
                            _teamTaskState.value.copy(loading = false, resultList = result.data)

                    }
                }
            }.launchIn(this)
        }
    }


    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }


}

data class TaskState(
    val loading: Boolean = false,
    val message: String? = null,
    val resultList: List<TaskModel>? = null,
    val isSuccessful: Boolean = false,
)

data class TaskDateState(
    val loading: Boolean = false,
    val message: String? = null,
    val dateList: List<String>? = null,
    val isSuccessful: Boolean = false,
)

data class WorkerState(
    val loading: Boolean = false,
    val message: String? = null,
    val resulListWithWorker: List<TaskModel>? = null,
    val isSuccessful: Boolean = false,
)

data class TeamTaskState(
    val resultList: List<String>? = null, val loading: Boolean = false
)



