package com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData

import com.muzo.sitesupervisor.core.data.model.DataModel
import javax.inject.Inject

class FireBaseRepositoryImpl @Inject constructor(private val fireBaseRepository: FireBaseRepository) : FireBaseRepository {

    override suspend fun saveData(data: DataModel) {
        fireBaseRepository.saveData(data =data)
    }

}