package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muzo.sitesupervisor.core.common.await
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import java.util.UUID
import javax.inject.Inject

class FireBaseSourceImpl @Inject constructor(
    private val database: FirebaseFirestore, private val storage: FirebaseStorage
) : FireBaseSource {


    init {
        FirebaseFirestore.setLoggingEnabled(true)
    }

    override suspend fun saveArea(dataModel: DataModel): Result<Unit> {

        return kotlin.runCatching {


            val currentUserRef = database.collection("Users").document(dataModel.currentUser)
            val constructionSiteRef =
                currentUserRef.collection("construcitonName").document(dataModel.constructionArea)

            currentUserRef.set(hashMapOf("dummyField" to "dummyValue")).await()
            constructionSiteRef.set(hashMapOf("dummyField" to "dummyValue")).await()

            val postsRef = constructionSiteRef.collection("posts").document(dataModel.id.toString())

            val post = hashMapOf(
                "message" to dataModel.message,
                "title" to dataModel.title,
                "photoUrl" to dataModel.photoUrl,
                "day" to dataModel.day,
                "time" to dataModel.time,
                "postId" to dataModel.id,
                "modificationDate" to dataModel.modificationDate,
                "modificationTime" to dataModel.modificationTime,
            )

            postsRef.set(post).await()
        }
    }

    override suspend fun fetchData(
        currentUser: String, constructionName: String, postId: String
    ): Result<List<DataModel>> {
        return kotlin.runCatching {
            val dataModelList = mutableListOf<DataModel>()

            val documentSnapshot =
                database.collection("Users").document(currentUser).collection("construcitonName")
                    .document(constructionName).collection("posts").document(postId).get()
                    .await()

            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data
                val message = data?.get("message") as? String ?: ""
                val title = data?.get("title") as? String ?: ""
                val photoUrl = data?.get("photoUrl") as? List<String> ?: listOf() // PhotoUrl artık liste
                val day = data?.get("day") as? String ?: ""
                val time = data?.get("time") as? String ?: ""
                val id = data?.get("id") as? Long ?: 0
                val modificationDate = data?.get("time") as? String ?: ""
                val modificationTime = data?.get("time") as? String ?: ""


                val retrievedDataModel = DataModel(
                    id = id, message, title, photoUrl, time, day, currentUser, constructionName,modificationDate,modificationTime
                )
                dataModelList.add(retrievedDataModel)
            }
            dataModelList.toList()
        }
    }

    override suspend fun fetchArea(): Result<List<UserConstructionData>> {
        return kotlin.runCatching {
            val userConstructionDataList = mutableListOf<UserConstructionData>()

            val usersSnapshot = database.collection("Users").get().await()

            for (userDocument in usersSnapshot.documents) {
                val constructionsSnapshot =
                    userDocument.reference.collection("construcitonName").get().await()

                val constructionList = mutableListOf<String>()
                for (constructionDocument in constructionsSnapshot.documents) {
                    val constructionArea = constructionDocument.id
                    constructionList.add(constructionArea)
                }

                val currentUser = userDocument.id
                val userData = UserConstructionData(currentUser, constructionList)
                userConstructionDataList.add(userData)
            }

            userConstructionDataList.toList()
        }
    }



    override suspend fun updateArea(dataModel: DataModel): Result<Unit> {
        return kotlin.runCatching {

            val currentUserRef = database.collection("Users").document(dataModel.currentUser)
            val constructionSiteRef =
                currentUserRef.collection("construcitonName").document(dataModel.constructionArea)

            val postsRef = constructionSiteRef.collection("posts").document(dataModel.id.toString())

            val post = hashMapOf(
                "message" to dataModel.message,
                "title" to dataModel.title,
                "photoUrl" to dataModel.photoUrl,
                "day" to dataModel.day,
                "time" to dataModel.time,
                "postId" to dataModel.id,
                "modificationDate" to dataModel.modificationDate,
                "modificationTime" to dataModel.modificationTime,
            )

            postsRef.set(post).await()
        }
    }



    override suspend fun getAllPost(currentUser: String, constructionName: String): Result<List<DataModel>> {
        return kotlin.runCatching {
            val dataModelList = mutableListOf<DataModel>()

            val postsSnapshot = database.collection("Users")
                .document(currentUser)
                .collection("construcitonName")
                .document(constructionName)
                .collection("posts")
                .get()
                .await()

            for (postDocument in postsSnapshot.documents) {
                val data = postDocument.data
                val message = data?.get("message") as? String ?: ""
                val title = data?.get("title") as? String ?: ""
                val photoUrl = data?.get("photoUrl") as? List<String> ?: listOf() // PhotoUrl artık liste
                val day = data?.get("day") as? String ?: ""
                val time = data?.get("time") as? String ?: ""
                val id = data?.get("postId") as? Long ?: 0
                val modificationDate = data?.get("time") as? String ?: ""
                val modificationTime = data?.get("time") as? String ?: ""

                val retrievedDataModel = DataModel(
                    id = id,
                    message = message,
                    title = title,
                    photoUrl = photoUrl,
                    day = day,
                    time = time,
                    currentUser = currentUser,
                    constructionArea = constructionName,
                    modificationDate=modificationDate,
                    modificationTime=modificationTime
                )
                dataModelList.add(retrievedDataModel)
            }

            dataModelList.toList()
        }
    }


    override suspend fun addImageToFirebaseStorage(fileUris: List<Uri>?, postId: String): Result<List<Uri>> {
        return kotlin.runCatching {
            val uploadedUris = mutableListOf<Uri>()
            fileUris?.let { uris ->
                for (uri in uris) {
                    val fileName = UUID.randomUUID().toString() // Farklı dosya adları oluşturmak için rastgele bir ad kullanılıyor
                    val fileRef = storage.reference.child("users").child(postId).child(fileName)

                    fileRef.putFile(uri).await()

                    val downloadUrl = fileRef.downloadUrl.await()
                    uploadedUris.add(downloadUrl)
                }
            }
            uploadedUris
        }
    }


    override suspend fun addImageUrlToFireStore(downloadUrls:List<Uri> , currentUser: String, constructionName: String, postId: String): Result<Unit> {
        return kotlin.runCatching {
            val currentUserRef = database.collection("Users").document(currentUser)
            val constructionSiteRef = currentUserRef.collection("construcitonName").document(constructionName)
            val postsRef = constructionSiteRef.collection("posts").document(postId)

            val dataList = downloadUrls.map { downloadUrl ->
                hashMapOf(
                    "photoUrl" to downloadUrl.toString() // Convert Uri to String
                )
            }
            postsRef.set(mapOf("photos" to dataList)).await()
        }
    }

    override suspend fun getImageUrlFromFireStore(currentUser: String, constructionName: String, postId: String): Result<List<String>> {
        return kotlin.runCatching {
            val currentUserRef = database.collection("Users").document(currentUser)
            val constructionSiteRef = currentUserRef.collection("construcitonName").document(constructionName)
            val postsRef = constructionSiteRef.collection("posts").document(postId).get().await()

            val imageUrlList = mutableListOf<String>()

            if (postsRef.exists()) {
                val data = postsRef.data
                val photoUrl = data?.get("photoUrl") as? String
                if (!photoUrl.isNullOrBlank()) {
                    imageUrlList.add(photoUrl)
                }
            }

            imageUrlList.toList()
        }
    }

}