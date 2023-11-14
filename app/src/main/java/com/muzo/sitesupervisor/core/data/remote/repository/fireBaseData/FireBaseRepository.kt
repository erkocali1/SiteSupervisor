package com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData

import com.muzo.sitesupervisor.core.data.model.DataModel

interface FireBaseRepository {

    suspend fun saveData(userId: String, area: String, data: DataModel): Result<Unit>

    suspend fun saveArea(userId: String, area: String): Result<Unit>


}