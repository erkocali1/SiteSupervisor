package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants.Companion.ERROR_MESSAGE
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FireBaseSaveDataUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator fun invoke(userId: String, area: String): Flow<Unit> {
        return flow {
            val result = repository.saveArea(userId, area)
            (result.getOrNull() ?: throw IllegalArgumentException(ERROR_MESSAGE)).also {
                emit(it)
            }

        }
    }
}






