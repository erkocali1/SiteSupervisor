package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import com.google.firebase.firestore.FirebaseFirestore
import com.muzo.sitesupervisor.core.common.await
import com.muzo.sitesupervisor.core.data.model.DataModel
import javax.inject.Inject

class FireBaseSourceImpl @Inject constructor(private val database: FirebaseFirestore) :
    FireBaseSource {


    override suspend fun saveArea(dataModel: DataModel): Result<Unit> {

        return kotlin.runCatching {

            val post = hashMapOf(
                "message" to dataModel.message,
                "title" to dataModel.title,
                "photoUrl" to dataModel.photoUrl,
                "timeStamp" to dataModel.timestamp

                )

            database.collection("Users")
                .document(dataModel.currentUser)
                .collection("construcitonName")
                .document(dataModel.constructionArea)
                .set(post)
                .await()
        }
    }


    override suspend fun fetchData(currentUser: String, constructionName: String): Result<List<DataModel>> {
        return kotlin.runCatching {
            val querySnapshot = database.collection("Users")
                .document(currentUser)
                .collection("construcitonName")
                .document(constructionName)
                .collection("specificCollection")
                .get()
                .await()

            val dataList = mutableListOf<DataModel>()
            for (document in querySnapshot.documents) {
                val message = document.getString("message") ?: ""
                val title = document.getString("title") ?: ""
                val photoUrl = document.getString("photoUrl") ?: ""
                val timeStamp = document.getLong("timeStamp") ?: 0L

                val dataModel = DataModel(message, title, photoUrl, timeStamp, currentUser, constructionName)
                dataList.add(dataModel)
            }
            Result.success(dataList)
        }.getOrElse {
            Result.failure(it)
        }
    }
}