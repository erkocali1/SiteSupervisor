package com.muzo.sitesupervisor.core.data.local.repository

import com.muzo.sitesupervisor.core.data.local.room.PostDao
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LocalPostRepositoryImpl @Inject constructor(private val dao: PostDao) :
    LocalPostRepository {
    override suspend fun savePost(postList: DataModel):Long {
        val insertedId = dao.savePost(postList)
        return insertedId
    }

    override suspend fun getPost(postId: Long): Flow<DataModel> {
        return dao.getPost(postId)
    }

    override suspend fun updatePost(postId: Long, urlToDelete: String) {
        val post = dao.getPost(postId).firstOrNull()
        post?.let {
            val updatedPhotoUrls = it.photoUrl?.toMutableList()
            updatedPhotoUrls?.remove(urlToDelete)
            it.photoUrl = updatedPhotoUrls

            dao.updatePost(it)
        }
    }

    override suspend fun updatePhoto(postId: Long, newPhotoUrl: List<String>) {
        return dao.updatePhotoUrl(postId,newPhotoUrl)
    }

    override suspend fun updatePostData(postId: Long, updatedData: DataModel) {
        val post = dao.getPost(postId).firstOrNull()
        post?.let {
            it.title = updatedData.title
            it.message = updatedData.message
            it.photoUrl = updatedData.photoUrl
            it.day = updatedData.day
            it.time = updatedData.time
            it.currentUser = updatedData.currentUser
            it.constructionArea = updatedData.constructionArea
            it.modificationDate = updatedData.modificationDate
            it.modificationTime = updatedData.modificationTime

            dao.updatePost(it)
        }
    }


    //--------------SaveTask

    override suspend fun saveTask(taskModel: TaskModel): Long {
        return dao.saveTask(taskModel)
    }



}