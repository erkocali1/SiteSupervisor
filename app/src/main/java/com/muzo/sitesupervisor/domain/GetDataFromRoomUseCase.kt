package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.data.local.repository.LocalPostRepository
import com.muzo.sitesupervisor.core.data.model.DataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDataFromRoomUseCase @Inject constructor(private val repository: LocalPostRepository) {

    suspend operator fun invoke(postId:Long): Flow<DataModel> {
        return flow {
            val result=repository.getPost(postId)
            emitAll(result)
        }

    }

}