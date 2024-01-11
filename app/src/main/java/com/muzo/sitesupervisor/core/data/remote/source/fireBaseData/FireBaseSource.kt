package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel

interface FireBaseSource {


    suspend fun saveArea(dataModel: DataModel): Result<Unit>


    suspend fun fetchData(currentUser: String, constructionName: String,postId:String): Result<DataModel>

    suspend fun fetchArea(): Result<List<UserConstructionData>>
    suspend fun getAllPost(currentUser: String, constructionName: String):Result<List<DataModel>>

    suspend fun addImageToFirebaseStorage(fileUris: List<Uri>?,postId: String): Result<List<Uri>>

    suspend fun addImageUrlToFireStore(downloadUrls: List<Uri>,currentUser: String,constructionName: String,postId: String):Result<Unit>

    suspend fun getImageUrlFromFireStore(currentUser: String,constructionName: String,postId: String):Result<List<String>>

    suspend fun deletePhotoUrlFromFireStore(currentUser: String, constructionName: String, postId: String, photoUrlToDelete: String): Result<Unit>

    //-----------------Task------------//
    suspend fun saveTask(taskModel: TaskModel): Result<Unit>

    suspend fun getAllTask(currentUser: String, constructionName: String,date:String ): Result<List<TaskModel>>

    suspend fun getTaskDate(currentUser: String, constructionName: String): Result<List<String>>

    suspend fun getTasksWithWorker(workerName: String):Result<List<TaskModel>>


    //-----------------Statistic------------//

    suspend fun saveStatisticInfo(workerInfoModel: WorkInfoModel):Result<Unit>

    suspend fun getStatisticForVocation(infoCurrentUser: String, constructionName: String, infoVocation: String): Result<List<WorkInfoModel>>


    //-----------------Location------------//
     suspend fun saveLocation(latLng: LatLng, currentUser: String, constructionName: String): Result<Unit>

    suspend fun uploadLocation(currentUser: String, constructionName: String): Result<Pair<String, String>>

    //-----------------Settings------------//

     suspend fun addUserImage(fileUri: Uri, siteSuperVisor: String): Result<Uri>

    suspend fun addImageUrlToFireStore(downloadUrl: Uri, currentUser: String, constructionName: String): Result<Unit>
}