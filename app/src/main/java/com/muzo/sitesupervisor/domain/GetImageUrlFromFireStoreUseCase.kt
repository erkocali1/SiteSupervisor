package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants.Companion.ERROR_MESSAGE
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.IllegalArgumentException
import javax.inject.Inject

class GetImageUrlFromFireStoreUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator fun invoke(currentUser: String, constructionName: String, postId: String): Flow<List<String>> {
        return flow {
            val result=repository.getImageUrlFromFireStore(currentUser, constructionName, postId)
            (result.getOrNull() ?: throw IllegalArgumentException(ERROR_MESSAGE)).also {
                emit(it)
            }
        }
    }
}