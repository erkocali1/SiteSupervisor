package com.muzo.sitesupervisor.feature.fragment.detailFragment

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.asReSource
import com.muzo.sitesupervisor.core.constans.Constants.Companion.ERROR_MESSAGE
import com.muzo.sitesupervisor.core.constans.Constants.Companion.OK_MESSAGE
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.local.repository.LocalPostRepository
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import com.muzo.sitesupervisor.domain.AddImageToFirebaseStorageUseCase
import com.muzo.sitesupervisor.domain.AddImageUrlToFireStoreUseCase
import com.muzo.sitesupervisor.domain.FireBaseSaveDataUseCase
import com.muzo.sitesupervisor.domain.GetDataFromRoomUseCase
import com.muzo.sitesupervisor.domain.GetDataUseCase
import com.muzo.sitesupervisor.domain.UpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class DetailFragmentViewModel @Inject constructor(
    private val updateUseCase: UpdateUseCase,
    authRepository: AuthRepository,
    private val getDataUseCase: GetDataUseCase,
    private val addDataUseCase: FireBaseSaveDataUseCase,
    private val localPostRepository: LocalPostRepository,
    private val addImageToFirebaseStorageUseCase: AddImageToFirebaseStorageUseCase,
    private val addImageUrlToFireStoreUseCase: AddImageUrlToFireStoreUseCase,
    private val dataStore: MyDataStore,
    private val getDataFromRoomUseCase: GetDataFromRoomUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<UpdateState> = MutableStateFlow(UpdateState())
    val uiState = _uiState


    val currentUser = authRepository.currentUser?.uid.toString()


    fun updateData(dataModel: DataModel) {


        viewModelScope.launch {
            updateUseCase(dataModel).asReSource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _uiState.value =
                            _uiState.value.copy(loading = false, message = ERROR_MESSAGE)
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

    fun addImageToFirebaseStorage(fileUris: List<Uri>?, postId: String) {
        viewModelScope.launch {
            addImageToFirebaseStorageUseCase(fileUris, postId).asReSource().onEach { result ->
                Log.d("hello", "fileUris=> $fileUris postıd=> $postId")
                when (result) {
                    is Resource.Error -> {
                        _uiState.value =
                            _uiState.value.copy(loading = false, message = ERROR_MESSAGE)
                    }

                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false, message = OK_MESSAGE, resultUriList = result.data
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    fun addImageUrlToFireStore(
        downloadUrl: List<Uri>, currentUser: String, constructionName: String, postId: String
    ) {
        viewModelScope.launch {
            addImageUrlToFireStoreUseCase(
                downloadUrl, currentUser, constructionName, postId
            ).asReSource().onEach { result ->
                when (result) {
                    is Resource.Error -> _uiState.value =
                        _uiState.value.copy(loading = false, message = ERROR_MESSAGE)

                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(loading = false, message = OK_MESSAGE)
                    }
                }
            }.launchIn(this)
        }
    }


    fun getCurrentDateAndTime(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)
        val currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
        return Pair(currentDate, currentTime)
    }

    private fun getData(constructionName: String, currentUser: String, postId: String) {

        viewModelScope.launch {
            getDataUseCase(constructionName, currentUser, postId).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _uiState.value =
                            _uiState.value.copy(message = ERROR_MESSAGE, loading = false)
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            message = OK_MESSAGE, loading = false, resultList = result.data
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    suspend fun saveRoom(saveList: DataModel): Long {
        val baba = localPostRepository.savePost(saveList)
        return baba
    }


    fun addData(dataModel: DataModel) {

        viewModelScope.launch {
            addDataUseCase(dataModel).asReSource().onEach { result ->
                when (result) {

                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Error -> {

                        _uiState.value =
                            _uiState.value.copy(message = ERROR_MESSAGE, loading = false)
                    }

                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            message = OK_MESSAGE, loading = false, isSuccessful = true
                        )
                    }
                }
            }.launchIn(this)
        }
    }

    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }

    fun getDataFromRoom(postId: Long) {
        viewModelScope.launch {
            getDataFromRoomUseCase(postId).asReSource().onEach { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(loading = true)
                    }

                    is Resource.Error -> {

                        _uiState.value =
                            _uiState.value.copy(message = ERROR_MESSAGE, loading = false)
                    }

                    is Resource.Success -> {
                        _uiState.value =
                            _uiState.value.copy(loading = false, localData = result.data)
                    }
                }
            }.launchIn(this)
        }
    }
}

data class UpdateState(
    val loading: Boolean = false,
    val message: String? = null,
    val isSuccessful: Boolean = false,
    val resultList: List<DataModel>? = null,
    val resultUriList: List<Uri>? = null,
    val localData: DataModel? = null
)
