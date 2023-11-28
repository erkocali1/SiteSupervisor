package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetPostIdsUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator  fun invoke(userId: String, constructionName: String): Flow<List<String>> {
        return flow {
            val result = repository.getConstructionSitePostIds(userId, constructionName)
            (result.getOrNull() ?: throw IllegalArgumentException(Constants.ERROR_MESSAGE)).also {
                emit(it)
            }
        }
    }
}