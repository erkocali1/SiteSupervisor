package com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData

import android.net.Uri
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.core.data.remote.source.fireBaseData.FireBaseSource
import javax.inject.Inject

class FireBaseRepositoryImpl @Inject constructor(private val fireBaseSource: FireBaseSource) :
    FireBaseRepository {


    override suspend fun saveArea(data: DataModel): Result<Unit> {
        return fireBaseSource.saveArea(data)
    }

    override suspend fun fetchData(
        currentUser: String,
        constructionName: String,
        postId: String
    ): Result<DataModel> {
        return fireBaseSource.fetchData(currentUser, constructionName, postId)
    }

    override suspend fun fetchArea(): Result<List<UserConstructionData>> {
        return fireBaseSource.fetchArea()
    }

    override suspend fun updateArea(dataModel: DataModel): Result<Unit> {
        return fireBaseSource.updateArea(dataModel)
    }

    override suspend fun getAllPost(currentUser: String, constructionName: String): Result<List<DataModel>> {
        return fireBaseSource.getAllPost(currentUser, constructionName)
    }

    override suspend fun addImageToFirebaseStorage(fileUris: List<Uri>?, postId: String): Result<List<Uri>> {
        return fireBaseSource.addImageToFirebaseStorage(fileUris, postId)
    }

    override suspend fun addImageUrlToFireStore(downloadUrl:List<Uri>, currentUser: String, constructionName: String, postId: String): Result<Unit> {
        return fireBaseSource.addImageUrlToFireStore(downloadUrl, currentUser, constructionName, postId)
    }

    override suspend fun getImageUrlFromFireStore(currentUser: String, constructionName: String, postId: String): Result<List<String>> {
        return fireBaseSource.getImageUrlFromFireStore(currentUser, constructionName, postId)
    }

    override suspend fun deletePhotoUrlFromFireStore(currentUser: String, constructionName: String, postId: String, photoUrlToDelete: String): Result<Unit> {
        return fireBaseSource.deletePhotoUrlFromFireStore(currentUser, constructionName, postId, photoUrlToDelete)
    }

    //-----------------Task------------------------\\
    override suspend fun saveTask(taskModel: TaskModel): Result<Unit> {
    return fireBaseSource.saveTask(taskModel)
    }

    override suspend fun getAllTask(currentUser: String, constructionName: String,date:String ): Result<List<TaskModel>> {
        return fireBaseSource.getAllTask(currentUser, constructionName,date)
    }

    override suspend fun getTaskDate(currentUser: String, constructionName: String): Result<List<String>> {
        return fireBaseSource.getTaskDate(currentUser, constructionName)
    }

    override suspend fun getTasksWithWorker(worker: String):Result<List<TaskModel>> {
        return fireBaseSource.getTasksWithWorker(worker)
    }


}

