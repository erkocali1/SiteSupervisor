package com.muzo.sitesupervisor.feature.fragment.statisticsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.DeleteStatisticUseCase
import com.muzo.sitesupervisor.domain.GetStatisticForVocationUseCase
import com.muzo.sitesupervisor.domain.GetTeamUseCase
import com.muzo.sitesupervisor.feature.fragment.taskFragment.TeamTaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveStatisticViewModel @Inject constructor(
    private val getStatisticForVocationUseCase: GetStatisticForVocationUseCase,
    private val dataStore: MyDataStore,
    private val getTeamUseCase: GetTeamUseCase,
    private val deleteStatisticUseCase: DeleteStatisticUseCase,
    authRepository: AuthRepository
) : ViewModel() {


    private val _uiState: MutableStateFlow<GetStatisticState> = MutableStateFlow(GetStatisticState())
    val uiState = _uiState

    private val _teamStatisticState: MutableStateFlow<TeamStaticState> = MutableStateFlow(TeamStaticState())
    val teamStatisticState = _teamStatisticState

    private val _deleteState: MutableStateFlow<DeleteState> = MutableStateFlow(DeleteState())
    val deleteState = _deleteState

    val currentUser = authRepository.currentUser?.uid.toString()


    fun getStatisticForVocation(infoCurrentUser: String, constructionName: String, infoVocation: String
    ) {

        viewModelScope.launch {
            getStatisticForVocationUseCase(
                infoCurrentUser,
                constructionName,
                infoVocation
            ).asReSource().onEach { result ->
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

    fun getTeam(currentUser: String, constructionName: String) {
        viewModelScope.launch {
            getTeamUseCase(currentUser, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _teamStatisticState.value = _teamStatisticState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _teamStatisticState.value = _teamStatisticState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _teamStatisticState.value =
                            _teamStatisticState.value.copy(loading = false, resultList = result.data)

                    }
                }
            }.launchIn(this)
        }
    }

    fun deleteStatistic(siteSuperVisor: String, constructionName: String, infoVocation: String, randomId: String) {
        viewModelScope.launch {
            deleteStatisticUseCase(siteSuperVisor, constructionName, infoVocation, randomId).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _deleteState.value = _deleteState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _deleteState.value = _deleteState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _deleteState.value = _deleteState.value.copy(loading = false, isDelete = true)

                    }
                }
            }.launchIn(this)
        }
    }



    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }
}

data class GetStatisticState(
    val loading: Boolean = false,
    val message: String? = null,
    val isSuccessful: Boolean = false,
    var resultList: List<WorkInfoModel>? = null
)

data class TeamStaticState(
    val resultList: List<String>? = null, val loading: Boolean = false
)

data class DeleteState(
    val loading: Boolean = false,
    var isDelete:Boolean=false
)