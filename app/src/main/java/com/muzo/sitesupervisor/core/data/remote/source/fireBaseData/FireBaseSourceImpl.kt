package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.currentComposer
import com.google.firebase.firestore.FirebaseFirestore
import com.muzo.sitesupervisor.core.common.await
import com.muzo.sitesupervisor.core.data.model.ConstructionName
import com.muzo.sitesupervisor.core.data.model.DataModel
import javax.inject.Inject

class FireBaseSourceImpl @Inject constructor(private val database: FirebaseFirestore) : FireBaseSource {


    init {
        FirebaseFirestore.setLoggingEnabled(true)
    }
    override suspend fun saveArea(dataModel: DataModel): Result<Unit> {
        return kotlin.runCatching {
            val post = hashMapOf(
                "message" to dataModel.message,
                "title" to dataModel.title,
                "photoUrl" to dataModel.photoUrl,
                "timeStamp" to dataModel.timestamp
            )

            val currentUserRef = database.collection("Users").document(dataModel.currentUser)
            val constructionSiteRef = currentUserRef.collection("construcitonName").document(dataModel.constructionArea)

            // Üst belgeyi oluşturmak
            currentUserRef.set(hashMapOf("dummyField" to "dummyValue")).await()
            constructionSiteRef.set(hashMapOf("dummyField" to "dummyValue")).await()

            val postsRef = constructionSiteRef.collection("posts").document("20231117")

            postsRef.set(post).await()
        }
    }




    override suspend fun fetchData(
        currentUser: String, constructionName: String
    ): Result<List<DataModel>> {
        return kotlin.runCatching {
            val dataModelList = mutableListOf<DataModel>()

            val documentSnapshot =
                database.collection("Users")
                    .document(currentUser)
                    .collection("construcitonName")
                    .document(constructionName)
                    .collection("posts")
                    .document("20231117")
                    .get()
                    .await()

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

            val usersSnapshot = database.collection("Users")
                .get().await()

            if (usersSnapshot.isEmpty){
                Log.d("FirebaseDebug", "usersSnapshot is empty")
            }

            for (userDocument in usersSnapshot.documents) {
                val userId = userDocument.id
                val constructionsSnapshot = userDocument.reference.collection("construcitonName").get().await()

                for (constructionDocument in constructionsSnapshot.documents) {
                    val data = constructionDocument.data
                    val message = data?.get("message") as? String ?: ""
                    val title = data?.get("title") as? String ?: ""
                    val photoUrl = data?.get("photoUrl") as? String ?: ""
                    val timestamp = data?.get("timeStamp") as? Number ?: 0

                    val constructionName = ConstructionName(
                        userId,
                        constructionDocument.id,
                        message,
                        title,
                        photoUrl,
                        timestamp.toLong()
                    )
                    constructionNames.add(constructionName)
                }
            }

            constructionNames.toList()
        }
    }

}