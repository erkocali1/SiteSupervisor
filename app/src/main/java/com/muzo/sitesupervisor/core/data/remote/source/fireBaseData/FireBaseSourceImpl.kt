package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import com.google.firebase.firestore.FirebaseFirestore
import com.muzo.sitesupervisor.core.common.await
import com.muzo.sitesupervisor.core.data.model.DataModel
import javax.inject.Inject

class FireBaseSourceImpl @Inject constructor(private val database: FirebaseFirestore) :
    FireBaseSource {
    override suspend fun saveData(userId: String, area: String, data: DataModel): Result<Unit> {

        return kotlin.runCatching {
            database.collection("Users")
                .document(userId)
                .collection("construcitonName")
                .document(area)
                .set(data)
                .await()

        }

    }

    override suspend fun saveArea(userId: String, area: String): Result<Unit> {
        return kotlin.runCatching {

            val data= hashMapOf(
                "data" to 5
            )

            database.collection("Users")
                .document(userId)
                .collection("construcitonName")
                .document(area)
                .set(data)
                .await()
        }
    }


    override suspend fun fetchData() {

    }
}