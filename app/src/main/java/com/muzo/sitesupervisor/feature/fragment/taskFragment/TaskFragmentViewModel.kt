package com.muzo.sitesupervisor.feature.fragment.taskFragment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.domain.GetAllTaskUseCase
import com.muzo.sitesupervisor.feature.fragment.detail.UpdateState
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
    private val dataStore: MyDataStore
) : ViewModel() {

    private val _uiState: MutableStateFlow<TaskState> = MutableStateFlow(TaskState())
    val uiState = _uiState




    fun getAllTask(currentUser: String, constructionName: String,date:String ) {
        viewModelScope.launch {
            getAllTaskUseCase(currentUser, constructionName,date).asReSource().onEach { result ->

                when (result) {
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(loading = false)
                    }

                    Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(loading = false, resultList = result.data)
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
