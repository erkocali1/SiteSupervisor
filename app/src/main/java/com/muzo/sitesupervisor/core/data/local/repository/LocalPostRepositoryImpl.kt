package com.muzo.sitesupervisor.core.data.local.repository

import com.muzo.sitesupervisor.core.data.local.room.PostDao
import com.muzo.sitesupervisor.core.data.model.DataModel
import javax.inject.Inject

class LocalPostRepositoryImpl @Inject constructor(private val dao: PostDao) :
    LocalPostRepository {
    override suspend fun savePost(postList: DataModel):Long {
        val insertedId = dao.savePost(postList)
        // Burada insertedId ile istediğiniz işlemleri yapabilirsiniz
        return insertedId
    }
}