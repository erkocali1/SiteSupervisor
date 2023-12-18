package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveTaskUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator fun invoke(taskModel: TaskModel): Flow<Unit> {
        return flow {
            val result = repository.saveTask(taskModel)
            (result.getOrNull() ?: throw IllegalArgumentException(Constants.ERROR_MESSAGE)).also {
                emit(it)
            }

        }
    }
}