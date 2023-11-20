package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAreaUseCase @Inject constructor(private val repository: FireBaseRepository) {


    suspend operator fun invoke(): Flow<List<UserConstructionData>> {

        return flow {

            val result = repository.fetchArea()
            (result.getOrNull() ?: throw IllegalArgumentException(Constants.ERROR_MESSAGE)).also {
                emit(it)
            }
        }
    }
}