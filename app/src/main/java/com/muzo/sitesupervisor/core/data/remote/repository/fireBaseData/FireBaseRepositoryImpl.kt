package com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData

import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.core.data.remote.source.fireBaseData.FireBaseSource
import javax.inject.Inject

class FireBaseRepositoryImpl @Inject constructor(private val fireBaseSource: FireBaseSource) :
    FireBaseRepository {


    override suspend fun saveArea(data: DataModel): Result<Unit> {
        return fireBaseSource.saveArea(data)
    }

    override suspend fun fetchData(currentUser: String, constructionName: String): Result<List<DataModel>> {
        return fireBaseSource.fetchData(currentUser, constructionName)
    }

    override suspend fun fetchArea():Result<List<UserConstructionData>>{
    return fireBaseSource.fetchArea()
    }

    override suspend fun updateArea(dataModel: DataModel): Result<Unit> {
        return fireBaseSource.updateArea(dataModel)
    }


}

