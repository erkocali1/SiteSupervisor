package com.muzo.sitesupervisor.feature.fragment.detail

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
import com.muzo.sitesupervisor.domain.FireBaseSaveDataUseCase
import com.muzo.sitesupervisor.domain.GetAllPostUseCase
import com.muzo.sitesupervisor.domain.GetDataFromRoomUseCase
import com.muzo.sitesupervisor.domain.GetDataUseCase
import com.muzo.sitesupervisor.domain.GetImageUrlFromFireStoreUseCase
import com.muzo.sitesupervisor.domain.UpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class DetailFragmentViewModel @Inject constructor(
    private val updateUseCase: UpdateUseCase,
    authRepository: AuthRepository,
    private val addDataUseCase: FireBaseSaveDataUseCase,
    private val localPostRepository: LocalPostRepository,
    private val addImageToFirebaseStorageUseCase: AddImageToFirebaseStorageUseCase,
    private val dataStore: MyDataStore,
    private val getDataFromRoomUseCase: GetDataFromRoomUseCase,
    private val getImageUrlFromFireStoreUseCase: GetImageUrlFromFireStoreUseCase,
    private val getDataUseCase: GetDataUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<UpdateState> = MutableStateFlow(UpdateState())
    val uiState = _uiState


    val currentUser = authRepository.currentUser?.uid.toString()


    fun getData(currentUser: String, constructionName: String, postId: String) {
        viewModelScope.launch {
            getDataUseCase(currentUser, constructionName, postId).asReSource().onEach { result ->

                when (result) {
                    Resource.Loading -> {
                        _uiState.value = UpdateState(loading = true)
                    }

                    is Resource.Error -> {
                        _uiState.value = UpdateState(loading = false, message = ERROR_MESSAGE)
                    }

                    is Resource.Success -> {
                        _uiState.value = UpdateState(
                            loading = false, message = OK_MESSAGE, getDataFireBase = result.data
                        )
                    }
                }
            }.launchIn(this)

        }
    }


    fun updateData(dataModel: DataModel) {

        viewModelScope.launch {
            updateUseCase(dataModel).asReSource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        _uiState.value = UpdateState(loading = true)
                    }

                    is Resource.Error -> {
                        _uiState.value = UpdateState(loading = false, message = ERROR_MESSAGE)
                    }

                    is Resource.Success -> {
                        _uiState.value = UpdateState(
                            loading = false, message = OK_MESSAGE, isSuccessfulUpdateData = true
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
                        _uiState.value = UpdateState(loading = false, message = ERROR_MESSAGE)
                    }

                    is Resource.Loading -> {
                        _uiState.value = UpdateState(loading = true)
                    }

                    is Resource.Success -> {
                        _uiState.value = UpdateState(
                            loading = false, message = OK_MESSAGE, resultUriList = result.data
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
        val result = localPostRepository.savePost(saveList)
        return result
    }

    suspend fun updatePhoto(postId: Long, newPhotoUrl: List<String>) {
        localPostRepository.updatePhoto(postId, newPhotoUrl)
    }

    suspend fun updateAllPost(postId: Long, dataModel: DataModel) {
        localPostRepository.updatePostData(postId, dataModel)
    }


    fun addData(dataModel: DataModel) {
        viewModelScope.launch {
            addDataUseCase(dataModel).asReSource().onEach { result ->
                when (result) {

                    is Resource.Loading -> {
                        _uiState.value = UpdateState(loading = true)
                    }

                    is Resource.Error -> {

                        _uiState.value = UpdateState(message = ERROR_MESSAGE, loading = false)
                    }

                    is Resource.Success -> {
                        _uiState.value = UpdateState(
                            message = OK_MESSAGE, loading = false, isSuccessfulAddData = true
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

    fun getAllPhoto(currentUser: String, constructionName: String, postId: String) {

        viewModelScope.launch {
            getImageUrlFromFireStoreUseCase(currentUser, constructionName, postId).asReSource()
                .onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            _uiState.value = UpdateState(loading = false)
                        }

                        Resource.Loading -> {
                            _uiState.value = UpdateState(loading = true)
                        }

                        is Resource.Success -> {
                            _uiState.value = UpdateState(loading = false, photoList = result.data)
                        }
                    }
                }.launchIn(this)
        }
    }
}

data class UpdateState(
    val loading: Boolean = false,
    val message: String? = null,
    val isSuccessfulUpdateData: Boolean = false,
    val isSuccessfulAddData: Boolean = false,
    val resultList: List<DataModel>? = null,
    val resultUriList: List<Uri>? = null,
    val localData: DataModel? = null,
    val photoList: List<String>? = null,
    val getDataFireBase: DataModel? = null,

)
