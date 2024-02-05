package com.muzo.sitesupervisor.feature.fragment.deleteAreaFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.domain.DeleteAreaUseCase
import com.muzo.sitesupervisor.feature.fragment.paswordFragment.DeleteState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAreaViewModel @Inject constructor(
    private val deleteAreaUseCase: DeleteAreaUseCase,
    private val dataStore: MyDataStore
) : ViewModel() {

    private val _deleteAreaState: MutableStateFlow<DeleteArea> = MutableStateFlow(DeleteArea())
    val deleteAreaState = _deleteAreaState

    fun deleteArea(currentUser: String, constructionName: String) {
        viewModelScope.launch {
            deleteAreaUseCase(currentUser, constructionName).asReSource()
                .onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _deleteAreaState.value = _deleteAreaState.value.copy(loading = false)
                        }

                        is Resource.Loading -> {
                            _deleteAreaState.value = _deleteAreaState.value.copy(loading = true)
                        }

                        is Resource.Success -> {
                            _deleteAreaState.value =
                                _deleteAreaState.value.copy(loading = false, result = true)
                        }
                    }
                }.launchIn(this)
        }
    }


    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }
}


data class DeleteArea(
    val loading: Boolean = false,
    val result: Boolean = false,
)