package com.muzo.sitesupervisor.feature.fragment.settingsFragment.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.domain.AddTeamUseCase
import com.muzo.sitesupervisor.domain.GetTeamUseCase
import com.muzo.sitesupervisor.domain.UpdateTeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamFragmentViewModel @Inject constructor(
    private val updateTeamUseCase: UpdateTeamUseCase, private val getTeamUseCase: GetTeamUseCase,
    private val dataStore: MyDataStore
) : ViewModel() {

    private val _addItemState: MutableStateFlow<AddTeamState> = MutableStateFlow(AddTeamState())
    val addItemState = _addItemState

    private val _getTeamState: MutableStateFlow<GetTeamState> = MutableStateFlow(GetTeamState())
    val getTeamState = _getTeamState


    fun updateItem(currentUser: String, teams: List<String>, constructionName: String) {
        viewModelScope.launch {
            updateTeamUseCase(currentUser, teams, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _addItemState.value = _addItemState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _addItemState.value = _addItemState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _addItemState.value =
                            _addItemState.value.copy(loading = false, result = true)

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
                        _getTeamState.value = _getTeamState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _getTeamState.value = _getTeamState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _getTeamState.value =
                            _getTeamState.value.copy(loading = false, resultList = result.data)

                    }
                }
            }.launchIn(this)
        }
    }

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }


}

data class AddTeamState(
    val result: Boolean = false, val loading: Boolean = false
)

data class GetTeamState(
    val resultList: List<String>? = null, val loading: Boolean = false
)

