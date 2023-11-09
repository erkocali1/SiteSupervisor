package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FireBaseSaveDataUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator fun invoke(dataModel: DataModel): Flow<Resource<Unit>> {
        return flow {
            repository.saveData(data = dataModel)
        }

    }

}






