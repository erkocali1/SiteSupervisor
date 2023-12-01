package com.muzo.sitesupervisor.core.data.local.repository

import com.muzo.sitesupervisor.core.data.local.room.PostDao
import com.muzo.sitesupervisor.core.data.model.DataModel
import kotlinx.coroutines.flow.Flow
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
}