package com.muzo.sitesupervisor.feature.fragment.createAreaFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.constans.Constants.Companion.OK_MESSAGE
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.local.repository.LocalPostRepository
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.AddTeamUseCase
import com.muzo.sitesupervisor.domain.FireBaseSaveDataUseCase
import com.muzo.sitesupervisor.domain.GetAreaUseCase
import com.muzo.sitesupervisor.feature.fragment.settingsFragment.team.AddTeamState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class CreateAreFragmentViewModel @Inject constructor(
    private val fireBaseSaveDataUseCase: FireBaseSaveDataUseCase,
    authRepository: AuthRepository,
    private val localPostRepository: LocalPostRepository,
    private val addTeamUseCase: AddTeamUseCase,
    private val myDataStore: MyDataStore,
    private val useCase: GetAreaUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<SaveDataState> = MutableStateFlow(SaveDataState())
    val uiState = _uiState

    private val _addItemState: MutableStateFlow<CreateTeamState> = MutableStateFlow(CreateTeamState())
    val addItemState=_addItemState

    private val _getOtherItemState: MutableStateFlow<GetArea> = MutableStateFlow(GetArea())
    val getOtherItemState=_getOtherItemState


    val currentUser = authRepository.currentUser


    init {
        getArea()
    }

    fun saveArea(dataModel: DataModel) {
        viewModelScope.launch {
            fireBaseSaveDataUseCase(dataModel).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false, message = result.exception?.message
                        )
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false, message = OK_MESSAGE, isSuccessful = true
                        )

                    }
                }
            }.launchIn(this)
        }

    }
    fun addItem(currentUser:String, teams: List<String>, constructionName: String) {
        viewModelScope.launch {
            addTeamUseCase(currentUser, teams, constructionName).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _addItemState.value = _addItemState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _addItemState.value = _addItemState.value.copy(loading = false,)
                    }

                    is Resource.Success -> {
                        _addItemState.value = _addItemState.value.copy(loading = false, result = true)

                    }
                }
            }.launchIn(this)
        }
    }
    private fun getArea() {
        viewModelScope.launch {
            useCase().asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _getOtherItemState.value = _getOtherItemState.value.copy(
                            loading = true
                        )
                    }

                    is Resource.Error -> {
                        _getOtherItemState.value = _getOtherItemState.value.copy(
                            loading = false, message = result.exception?.message
                        )
                    }

                    is Resource.Success -> {
                        _getOtherItemState.value = _getOtherItemState.value.copy(
                            loading = false,
                            resultList = result.data
                        )

                    }
                }
            }.launchIn(this)
        }
    }

    fun getCurrentDateAndTime(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val turkishLocale = Locale("tr", "TR")
        val currentDate =
            DateFormat.getDateInstance(DateFormat.FULL, turkishLocale).format(calendar.time)
        val currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
        return Pair(currentDate, currentTime)
    }

    suspend fun saveRoom(saveList: DataModel): Long {
        val room = localPostRepository.savePost(saveList)
        return room
    }

    suspend fun saveDataStore(currentUser: String, constructionArea: String) {
        myDataStore.saveData("user_key", "construction_key", currentUser, constructionArea)
    }


}

data class SaveDataState(
    val loading: Boolean = false,
    val message: String? = null,
    val isSuccessful: Boolean = false
)
data class CreateTeamState(
    val result: Boolean = false,
    val loading: Boolean = false
)

data class GetArea(
    val loading: Boolean = false,
    val message: String? = null,
    val resultList: List<UserConstructionData>? = null
)
