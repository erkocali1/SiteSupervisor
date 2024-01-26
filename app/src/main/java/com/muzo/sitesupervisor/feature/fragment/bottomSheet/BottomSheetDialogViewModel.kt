package com.muzo.sitesupervisor.feature.fragment.bottomSheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel
import com.muzo.sitesupervisor.domain.GetTeamUseCase
import com.muzo.sitesupervisor.domain.SaveStatisticUseCase
import com.muzo.sitesupervisor.feature.fragment.statisticsFragment.TeamStaticState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetDialogViewModel @Inject constructor(
    private val saveStatisticUseCase: SaveStatisticUseCase,
    private val dataStore: MyDataStore,
    private val getTeamUseCase: GetTeamUseCase
) : ViewModel() {


    private val _uiState: MutableStateFlow<SaveStatisticState> = MutableStateFlow(SaveStatisticState())
    val uiState = _uiState

    private val _getTeamBottomState: MutableStateFlow<TeamBottomState> = MutableStateFlow(TeamBottomState())
    val getTeamBottomState = _getTeamBottomState


    fun saveStatistic(workInfoModel: WorkInfoModel) {
        viewModelScope.launch {

            saveStatisticUseCase(workInfoModel).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(loading = false)
                    }

                    Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(loading = false, isSuccessful = true)
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
                        _getTeamBottomState.value = _getTeamBottomState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _getTeamBottomState.value = _getTeamBottomState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _getTeamBottomState.value =
                            _getTeamBottomState.value.copy(loading = false, resultList = result.data)

                    }
                }
            }.launchIn(this)
        }
    }


    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }

}
data class SaveStatisticState(
    val loading: Boolean = false,
    var isSuccessful: Boolean = false,
)

data class TeamBottomState(
    val resultList: List<String>? = null, val loading: Boolean = false
)