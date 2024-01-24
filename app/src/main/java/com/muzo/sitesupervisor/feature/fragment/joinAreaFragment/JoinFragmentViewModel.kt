package com.muzo.sitesupervisor.feature.fragment.joinAreaFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.GetAreaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class JoinFragmentViewModel @Inject constructor(
    private val useCase: GetAreaUseCase, private val myDataStore: MyDataStore,
    authRepository: AuthRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<GetAreaState> = MutableStateFlow(GetAreaState())
    val uiState = _uiState

    val currentUser = authRepository.currentUser?.uid.toString()


    init {
        getArea()
    }

    private fun getArea() {
        viewModelScope.launch {
            useCase().asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            loading = true
                        )
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false, message = result.exception?.message
                        )
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            message = Constants.OK_MESSAGE,
                            isSuccessful = true,
                            resultList = result.data
                        )

                    }
                }
            }.launchIn(this)
        }
    }

    suspend fun saveDataStore(currentUser: String, constructionArea: String) {
        myDataStore.saveData("user_key", "construction_key", currentUser, constructionArea)
    }

}

data class GetAreaState(
    val loading: Boolean = false,
    val message: String? = null,
    val isSuccessful: Boolean = false,
    val resultList: List<UserConstructionData>? = null
)



