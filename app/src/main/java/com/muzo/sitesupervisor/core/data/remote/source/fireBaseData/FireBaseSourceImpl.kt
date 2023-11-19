package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import com.google.firebase.firestore.FirebaseFirestore
import com.muzo.sitesupervisor.core.common.await
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
                "day" to dataModel.day,
                "time" to dataModel.time

            )

            val currentUserRef = database.collection("Users").document(dataModel.currentUser)
            val constructionSiteRef = currentUserRef.collection("construcitonName").document(dataModel.constructionArea)


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
                val day = data?.get("timeStamp") as? String ?: ""
                val time = data?.get("timeStamp") as? String ?: ""

                val retrievedDataModel = DataModel(
                    message, title, photoUrl, day,time, currentUser, constructionName
                )
                dataModelList.add(retrievedDataModel)
            }

            dataModelList.toList()
        }
    }

    override suspend fun fetchArea(): Result<List<String>> {
        return kotlin.runCatching {
            val constructionAreas = mutableListOf<String>()

            val usersSnapshot = database.collection("Users").get().await()

            for (userDocument in usersSnapshot.documents) {
                val constructionsSnapshot =
                    userDocument.reference.collection("construcitonName").get().await()

                for (constructionDocument in constructionsSnapshot.documents) {
                    val constructionArea = constructionDocument.id
                    constructionAreas.add(constructionArea)
                }
            }

            constructionAreas.toList()
        }
    }

    override suspend fun updateArea(dataModel: DataModel): Result<Unit> {
        return kotlin.runCatching {
            val post: MutableMap<String, Any> = hashMapOf(
                "message" to dataModel.message,
                "title" to dataModel.title,
                "photoUrl" to dataModel.photoUrl,
                "day" to dataModel.day,
                "time" to dataModel.time
            ).toMutableMap()
            val currentUserRef = database.collection("Users").document(dataModel.currentUser)
            val constructionSiteRef = currentUserRef.collection("construcitonName")
                .document(dataModel.constructionArea)

            val postsRef = constructionSiteRef.collection("posts").document("20231117")

            postsRef.update(post).await()
        }
    }


}