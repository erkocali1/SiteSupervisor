package com.muzo.sitesupervisor.core.data.local.repository

import com.muzo.sitesupervisor.core.data.model.DataModel
import kotlinx.coroutines.flow.Flow

interface LocalPostRepository {

    suspend fun savePost(postList:DataModel):Long

    suspend fun getPost(postId:Long) : Flow<DataModel>



}