package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class DeletePhotoUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator fun invoke(
        currentUser: String, constructionName: String, postId: String, photoUrlToDelete: String
    ): Flow<Unit> {
        return flow {
            val result = repository.deletePhotoUrlFromFireStore(
                currentUser, constructionName, postId, photoUrlToDelete
            )
            (result.getOrNull() ?: throw IllegalArgumentException(Constants.ERROR_MESSAGE)).also {
                emit(it)
            }
        }
    }

}