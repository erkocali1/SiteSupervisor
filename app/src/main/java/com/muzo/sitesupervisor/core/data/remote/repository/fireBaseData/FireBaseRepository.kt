package com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData

import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.data.model.DataModel

interface FireBaseRepository {

    suspend fun saveData(data: DataModel):Result<Unit>

}