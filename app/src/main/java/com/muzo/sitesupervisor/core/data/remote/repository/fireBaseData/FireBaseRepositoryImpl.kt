package com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData

import android.net.Uri
import com.muzo.sitesupervisor.core.data.model.DataModel
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
    ): Result<List<DataModel>> {
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


}

