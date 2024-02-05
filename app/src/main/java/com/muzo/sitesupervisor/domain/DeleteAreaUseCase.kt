package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants.Companion.ERROR_MESSAGE
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteAreaUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator fun invoke(currentUser: String, constructionName: String): Flow<Unit> {
        return flow {
            val result=repository.deleteArea(currentUser, constructionName)
            (result.getOrNull() ?:throw IllegalArgumentException(ERROR_MESSAGE)).also { emit(it) }
        }
    }

}