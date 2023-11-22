package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import android.net.Uri
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData

interface FireBaseSource {


    suspend fun saveArea(dataModel: DataModel): Result<Unit>


    suspend fun fetchData(currentUser: String, constructionName: String,postId:String): Result<List<DataModel>>

    suspend fun fetchArea(): Result<List<UserConstructionData>>

    suspend fun updateArea(dataModel: DataModel): Result<Unit>

    suspend fun upLoadImage(fileUri: List<Uri>): Result<Unit>

    suspend fun getImageUrl(imagePath: String): Result<Uri>

    suspend fun getAllPost(currentUser: String, constructionName: String):Result<List<DataModel>>


}