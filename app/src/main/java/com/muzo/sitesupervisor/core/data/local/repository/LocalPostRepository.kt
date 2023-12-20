package com.muzo.sitesupervisor.core.data.local.repository

import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import kotlinx.coroutines.flow.Flow

interface LocalPostRepository {

    suspend fun savePost(postList: DataModel): Long

    suspend fun getPost(postId: Long): Flow<DataModel>

    suspend fun updatePostData(postId: Long, updatedData: DataModel)

    suspend fun updatePost(postId: Long, urlToDelete: String)
    suspend fun updatePhoto(postId: Long, newPhotoUrl: List<String>)

    //------------Save Task

    suspend fun saveTask(taskModel: TaskModel): Long


}