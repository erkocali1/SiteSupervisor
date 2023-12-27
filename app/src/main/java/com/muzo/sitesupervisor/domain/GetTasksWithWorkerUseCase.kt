package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants.Companion.ERROR_MESSAGE
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTasksWithWorkerUseCase @Inject constructor(private val repository: FireBaseRepository) {
    suspend operator fun invoke(worker: String): Flow<List<TaskModel>> {

        return flow {
            val result = repository.getTasksWithWorker(worker)
            (result.getOrNull() ?: throw IllegalArgumentException(ERROR_MESSAGE)).also {
                emit(it)
            }
        }

    }
}