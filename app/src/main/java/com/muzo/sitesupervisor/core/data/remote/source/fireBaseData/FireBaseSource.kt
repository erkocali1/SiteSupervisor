package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import com.muzo.sitesupervisor.core.data.model.DataModel

interface FireBaseSource {


    suspend fun saveArea(dataModel: DataModel): Result<Unit>


    suspend fun fetchData(currentUser: String, constructionName: String): Result<List<DataModel>>


}