package com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData

import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.remote.source.fireBaseData.FireBaseSource
import javax.inject.Inject

class FireBaseRepositoryImpl @Inject constructor(private val fireBaseSource: FireBaseSource) :
    FireBaseRepository {
    override suspend fun saveData(userId: String, area: String, data: DataModel): Result<Unit> {
        return fireBaseSource.saveData(userId, area, data)
    }

    override suspend fun saveArea(userId: String, area: String): Result<Unit> {
        return fireBaseSource.saveArea(userId, area)
    }


}

