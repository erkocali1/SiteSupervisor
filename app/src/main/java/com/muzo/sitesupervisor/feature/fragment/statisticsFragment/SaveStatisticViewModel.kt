package com.muzo.sitesupervisor.feature.fragment.statisticsFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel
import com.muzo.sitesupervisor.domain.GetStatisticForVocationUseCase
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
    private val dataStore: MyDataStore
) : ViewModel() {


    private val _uiState: MutableStateFlow<GetStatisticState> =
        MutableStateFlow(GetStatisticState())
    val uiState = _uiState


    fun getStatisticForVocation(
        infoCurrentUser: String,
        constructionName: String,
        infoVocation: String
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

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }


}

data class GetStatisticState(
    val loading: Boolean = false,
    val message: String? = null,
    val isSuccessful: Boolean = false,
    val resultList: List<WorkInfoModel>? = null
)