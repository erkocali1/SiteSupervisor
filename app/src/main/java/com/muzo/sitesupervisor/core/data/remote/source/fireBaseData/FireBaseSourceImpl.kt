package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import com.google.firebase.firestore.FirebaseFirestore
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.await
import com.muzo.sitesupervisor.core.data.model.DataModel
import javax.inject.Inject

class FireBaseSourceImpl @Inject constructor(private val database: FirebaseFirestore) :
    FireBaseSource {
    override suspend fun saveData(data: DataModel): Result<Unit> {

        return kotlin.runCatching {
            database.collection(data.collection).add(data).await()
        }

    }




    override suspend fun fetchData() {

    }
}