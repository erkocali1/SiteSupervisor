package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.data.model.DataModel

interface FireBaseSource {

    suspend fun saveData(data: DataModel):Resource<Unit>


    suspend fun fetchData()


}