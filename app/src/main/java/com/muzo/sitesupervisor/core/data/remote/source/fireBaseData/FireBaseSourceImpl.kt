package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import com.google.firebase.firestore.FirebaseFirestore
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.data.model.DataModel
import javax.inject.Inject

class FireBaseSourceImpl @Inject constructor(private val database: FirebaseFirestore) :
    FireBaseSource {
    override suspend fun saveData(data: DataModel): Resource<Unit> {

        return try {
            database.collection(data.collection).add(data)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }

    }

    override suspend fun fetchData() {

    }
}