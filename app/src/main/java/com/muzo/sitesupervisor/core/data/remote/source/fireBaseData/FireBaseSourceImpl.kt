package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.muzo.sitesupervisor.core.common.await
import com.muzo.sitesupervisor.core.data.model.ConstructionName
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

            database.collection("Users").document(dataModel.currentUser)
                .collection("construcitonName").document(dataModel.constructionArea).set(post)
                .await()
        }
    }


    override suspend fun fetchData(
        currentUser: String, constructionName: String
    ): Result<List<DataModel>> {
        return kotlin.runCatching {
            val dataModelList = mutableListOf<DataModel>()

            val documentSnapshot =
                database.collection("Users").document(currentUser).collection("construcitonName")
                    .document(constructionName).get().await()

            Log.d("FirebaseDebug", "$documentSnapshot")

            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data
                val message = data?.get("message") as? String ?: ""
                val title = data?.get("title") as? String ?: ""
                val photoUrl = data?.get("photoUrl") as? String ?: ""
                val timestamp = data?.get("timeStamp") as? Long ?: 0L

                val retrievedDataModel = DataModel(
                    message, title, photoUrl, timestamp, currentUser, constructionName
                )
                dataModelList.add(retrievedDataModel)
            }

            dataModelList.toList()
        }
    }

    override suspend fun fetchArea(): Result<List<ConstructionName>> {
        return kotlin.runCatching {
            val constructionNames = mutableListOf<ConstructionName>()

            val usersCollection = database.collection("Users")
                .get()
                .await()

            usersCollection.documents

            if (!usersCollection.isEmpty) {
                for (userDoc in usersCollection) {
                    val userId = userDoc.id
                    val constructionNameModel = ConstructionName("", userId)
                    constructionNames.add(constructionNameModel)
                    Log.d("FirebaseDebug", "Hello Africa")
                }
            } else {
                Log.d("FirebaseDebug", "Users collection is empty!")
            }

            constructionNames.toList()
        }
    }






}