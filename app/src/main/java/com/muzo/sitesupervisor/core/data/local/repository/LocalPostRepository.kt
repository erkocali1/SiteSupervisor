package com.muzo.sitesupervisor.core.data.local.repository

import com.muzo.sitesupervisor.core.data.model.DataModel

interface LocalPostRepository {

    suspend fun savePost(postList:DataModel):Long

}