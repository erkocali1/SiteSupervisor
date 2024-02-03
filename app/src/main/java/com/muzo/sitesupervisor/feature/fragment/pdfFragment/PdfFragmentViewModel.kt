package com.muzo.sitesupervisor.feature.fragment.pdfFragment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.domain.GetAllPostUseCase
import com.muzo.sitesupervisor.domain.GetAllTaskUseCase
import com.muzo.sitesupervisor.feature.fragment.listingNotes.GetDataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PdfFragmentViewModel @Inject constructor(
    private val getAllPostUseCase: GetAllPostUseCase,
    private val dataStore: MyDataStore
) : ViewModel() {

    private val _dataState: MutableStateFlow<PdfState> = MutableStateFlow(PdfState())
    val dataState = _dataState

    fun getAllData(currentUser: String, constructionName: String) {

        viewModelScope.launch {
            getAllPostUseCase(currentUser, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _dataState.value = _dataState.value.copy(loading = false)
                    }

                    Resource.Loading -> {
                        _dataState.value = _dataState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _dataState.value = _dataState.value.copy(
                            loading = false,
                            resultList = result.data
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }
}

data class PdfState(
    val loading: Boolean = false,
    val resultList: List<DataModel>? = null,
)