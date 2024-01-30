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
import com.muzo.sitesupervisor.domain.DeletePostUseCase
import com.muzo.sitesupervisor.domain.FireBaseSaveDataUseCase
import com.muzo.sitesupervisor.domain.GetDataFromRoomUseCase
import com.muzo.sitesupervisor.domain.GetDataUseCase
import com.muzo.sitesupervisor.domain.GetImageUrlFromFireStoreUseCase
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
    authRepository: AuthRepository,
    private val addDataUseCase: FireBaseSaveDataUseCase,
    private val localPostRepository: LocalPostRepository,
    private val addImageToFirebaseStorageUseCase: AddImageToFirebaseStorageUseCase,
    private val dataStore: MyDataStore,
    private val getDataFromRoomUseCase: GetDataFromRoomUseCase,
    private val getImageUrlFromFireStoreUseCase: GetImageUrlFromFireStoreUseCase,
    private val getDataUseCase: GetDataUseCase,
    private val deletePostUseCase:DeletePostUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<UpdateState> = MutableStateFlow(UpdateState())
    val uiState = _uiState

    private val _deleteState: MutableStateFlow<DeletePostState> = MutableStateFlow(DeletePostState())
    val deleteState = _deleteState


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

    fun deletePost(currentUser: String, constructionName: String, postId: String) {
        viewModelScope.launch {
            deletePostUseCase(currentUser, constructionName, postId).asReSource().onEach { result ->

                when (result) {
                    Resource.Loading -> {
                        _deleteState.value = _deleteState.value.copy(loading = true)
                    }

                    is Resource.Error -> {
                        _deleteState.value = _deleteState.value.copy(loading = false)
                    }

                    is Resource.Success -> {
                        _deleteState.value = _deleteState.value.copy(loading = false,isDelete = true)
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

data class DeletePostState(
    val loading: Boolean = false,
    val isDelete:Boolean=false
)
