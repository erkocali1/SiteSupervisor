package com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData

import android.net.Uri
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel

interface FireBaseRepository {



    suspend fun saveArea(data: DataModel): Result<Unit>

    suspend fun fetchData(currentUser: String, constructionName: String,postId:String):Result<DataModel>

    suspend fun fetchArea():Result<List<UserConstructionData>>

    suspend fun getAllPost(currentUser: String, constructionName: String):Result<List<DataModel>>

    suspend fun addImageToFirebaseStorage(fileUris: List<Uri>?,postId: String): Result<List<Uri>>

    suspend fun addImageUrlToFireStore(downloadUrl: List<Uri>,currentUser: String,constructionName: String,postId: String):Result<Unit>

    suspend fun getImageUrlFromFireStore(currentUser: String,constructionName: String,postId: String):Result<List<String>>

    suspend fun deletePhotoUrlFromFireStore(currentUser: String, constructionName: String, postId: String, photoUrlToDelete: String): Result<Unit>

    //-----------------Task
    suspend fun saveTask(taskModel: TaskModel): Result<Unit>

    suspend fun getAllTask(currentUser: String, constructionName: String,date:String ): Result<List<TaskModel>>

    suspend fun getTaskDate(currentUser: String, constructionName: String): Result<List<String>>

    suspend fun getTasksWithWorker(workerName: String):Result<List<TaskModel>>

    //-----------------Statistic------------\\

    suspend fun saveStatisticInfo(workerInfoModel: WorkInfoModel):Result<Unit>

    suspend fun getStatisticForVocation(infoCurrentUser: String, constructionName: String, infoVocation: String): Result<List<WorkInfoModel>>


}