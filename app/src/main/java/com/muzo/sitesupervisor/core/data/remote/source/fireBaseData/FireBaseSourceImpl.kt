package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muzo.sitesupervisor.core.common.await
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel
import java.util.UUID
import javax.inject.Inject

class FireBaseSourceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage
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
    ): Result<DataModel> {
        return kotlin.runCatching {
            val documentSnapshot =
                database.collection("Users").document(currentUser).collection("construcitonName")
                    .document(constructionName).collection("posts").document(postId).get().await()

            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data
                val message = data?.get("message") as? String ?: ""
                val title = data?.get("title") as? String ?: ""
                val photoUrl = data?.get("photoUrl") as? List<String> ?: listOf()
                val day = data?.get("day") as? String ?: ""
                val time = data?.get("time") as? String ?: ""
                val id = data?.get("id") as? Long ?: 0
                val modificationDate = data?.get("modificationDate") as? String ?: ""
                val modificationTime = data?.get("modificationTime") as? String ?: ""

                DataModel(
                    id = id,
                    message = message,
                    title = title,
                    photoUrl = photoUrl,
                    time = time,
                    day = day,
                    currentUser = currentUser,
                    constructionArea = constructionName,
                    modificationDate = modificationDate,
                    modificationTime = modificationTime
                )
            } else {
                throw NoSuchElementException("Document not found")
            }
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


    override suspend fun getAllPost(
        currentUser: String, constructionName: String
    ): Result<List<DataModel>> {
        return kotlin.runCatching {
            val dataModelList = mutableListOf<DataModel>()

            val postsSnapshot =
                database.collection("Users").document(currentUser).collection("construcitonName")
                    .document(constructionName).collection("posts").get().await()

            for (postDocument in postsSnapshot.documents) {
                val data = postDocument.data
                val message = data?.get("message") as? String ?: ""
                val title = data?.get("title") as? String ?: ""
                val photoUrl =
                    data?.get("photoUrl") as? List<String> ?: listOf() // PhotoUrl artık liste
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
                    modificationDate = modificationDate,
                    modificationTime = modificationTime
                )
                dataModelList.add(retrievedDataModel)
            }

            dataModelList.toList()
        }
    }


    override suspend fun addImageToFirebaseStorage(
        fileUris: List<Uri>?, postId: String
    ): Result<List<Uri>> {
        return kotlin.runCatching {
            val uploadedUris = mutableListOf<Uri>()
            fileUris?.let { uris ->
                for (uri in uris) {
                    val fileName = UUID.randomUUID()
                        .toString() // Farklı dosya adları oluşturmak için rastgele bir ad kullanılıyor
                    val fileRef = storage.reference.child("users").child(postId).child(fileName)

                    fileRef.putFile(uri).await()

                    val downloadUrl = fileRef.downloadUrl.await()
                    uploadedUris.add(downloadUrl)
                }
            }
            uploadedUris
        }
    }


    override suspend fun addImageUrlToFireStore(
        downloadUrls: List<Uri>, currentUser: String, constructionName: String, postId: String
    ): Result<Unit> {
        return kotlin.runCatching {
            val currentUserRef = database.collection("Users").document(currentUser)
            val constructionSiteRef =
                currentUserRef.collection("construcitonName").document(constructionName)
            val postsRef = constructionSiteRef.collection("posts").document(postId)

            val dataList = downloadUrls.map { downloadUrl ->
                hashMapOf(
                    "photoUrl" to downloadUrl.toString() // Convert Uri to String
                )
            }
            postsRef.set(mapOf("photos" to dataList)).await()
        }
    }

    override suspend fun getImageUrlFromFireStore(
        currentUser: String, constructionName: String, postId: String
    ): Result<List<String>> {
        return kotlin.runCatching {

            val postsSnapshot =
                database.collection("Users").document(currentUser).collection("construcitonName")
                    .document(constructionName).collection("posts").document(postId).get().await()

            val imageUrlList = mutableListOf<String>()

            if (postsSnapshot.exists()) {
                val data = postsSnapshot.data
                val photoUrl = data?.get("photoUrl") as? List<String> ?: listOf()
                imageUrlList.addAll(photoUrl)
            }

            imageUrlList
        }
    }

    override suspend fun deletePhotoUrlFromFireStore(
        currentUser: String, constructionName: String, postId: String, photoUrlToDelete: String
    ): Result<Unit> {
        return kotlin.runCatching {
            // Belirli URL'yi içeren dokümanı bul
            val postDocRef =
                database.collection("Users").document(currentUser).collection("construcitonName")
                    .document(constructionName).collection("posts").document(postId)

            val postSnapshot = postDocRef.get().await()

            if (postSnapshot.exists()) {
                val data = postSnapshot.data
                val photoUrlList = data?.get("photoUrl") as? List<String> ?: listOf()

                // Silinecek URL'yi içeren listeyi güncelle
                val updatedPhotoUrlList = photoUrlList.toMutableList()
                updatedPhotoUrlList.remove(photoUrlToDelete)

                // Firestore'daki belirli URL'yi kaldır
                postDocRef.update("photoUrl", updatedPhotoUrlList).await()
            } else {
                throw NoSuchElementException("Post not found")
            }
        }
    }

    //-----------------Task//-----------------\\
    override suspend fun saveTask(taskModel: TaskModel): Result<Unit> {

        return kotlin.runCatching {


            val currentUserRef = database.collection("Users").document(taskModel.currentUser)
            val constructionSiteRef =
                currentUserRef.collection("construcitonName").document(taskModel.constructionArea)
            val postsRef =
                constructionSiteRef.collection("task").document(taskModel.day)

            currentUserRef.set(hashMapOf("dummyField" to "dummyValue")).await()
            constructionSiteRef.set(hashMapOf("dummyField" to "dummyValue")).await()
            postsRef.set(hashMapOf("dummyField" to "dummyValue")).await()

            val taskRef = postsRef.collection("taskId").document(taskModel.taskId.toString())

            val post = hashMapOf(
                "message" to taskModel.message,
                "title" to taskModel.title,
                "day" to taskModel.day,
                "taskId" to taskModel.taskId,
                "workerList" to taskModel.workerList,
                "currentUser" to taskModel.currentUser,
                "constructionArea" to taskModel.constructionArea,
            )

            taskRef.set(post).await()
        }
    }

    override suspend fun getAllTask(
        currentUser: String,
        constructionName: String,
        date: String
    ): Result<List<TaskModel>> {
        return kotlin.runCatching {
            val dataModelList = mutableListOf<TaskModel>()

            val postsSnapshot =
                database.collection("Users").document(currentUser).collection("construcitonName")
                    .document(constructionName).collection("task").document(date)
                    .collection("taskId")
                    .get().await()

            for (postDocument in postsSnapshot.documents) {
                val data = postDocument.data
                val message = data?.get("message") as? String ?: ""
                val title = data?.get("title") as? String ?: ""
                val workerList =
                    data?.get("workerList") as? List<String> ?: listOf() // PhotoUrl artık liste
                val day = data?.get("day") as? String ?: ""
                val taskId = data?.get("taskId") as? Long ?: 0

                val retrievedTaskModel = TaskModel(
                    taskId = taskId,
                    message = message,
                    title = title,
                    workerList = workerList,
                    day = day,
                    currentUser = currentUser,
                    constructionArea = constructionName,
                )
                dataModelList.add(retrievedTaskModel)
            }

            dataModelList.toList()
        }
    }

    override suspend fun getTaskDate(
        currentUser: String,
        constructionName: String
    ): Result<List<String>> {
        return kotlin.runCatching {

            val taskDate = mutableListOf<String>()
            val dataSnapshot =
                database.collection("Users").document(currentUser).collection("construcitonName")
                    .document(constructionName).collection("task").get().await()

            for (dataDocument in dataSnapshot.documents) {
                val date = dataDocument.id
                taskDate.add(date)
            }
            taskDate.toList()
        }
    }



    override suspend fun getTasksWithWorker(workerName: String): Result<List<TaskModel>> {
        return kotlin.runCatching {
            val tasks = mutableListOf<TaskModel>()

            val usersCollection = database.collection("Users")
            val userSnapshots = usersCollection.get().await()

            for (userSnapshot in userSnapshots) {
                val constructionSiteCollection =
                    userSnapshot.reference.collection("construcitonName")
                val constructionSiteSnapshots = constructionSiteCollection.get().await()

                for (constructionSiteSnapshot in constructionSiteSnapshots) {
                    val taskCollection = constructionSiteSnapshot.reference.collection("task")
                    val taskSnapshots = taskCollection.get().await()

                    for (taskSnapshot in taskSnapshots) {
                        val taskQuery = taskSnapshot.reference.collection("taskId")
                            .whereArrayContains("workerList", workerName)
                        val taskQuerySnapshot = taskQuery.get().await()

                        for (taskDoc in taskQuerySnapshot) {
                            val data = taskDoc.data
                            val message = data["message"] as? String ?: ""
                            val title = data["title"] as? String ?: ""
                            val workerList = data["workerList"] as? List<String> ?: listOf()
                            val day = data["day"] as? String ?: ""
                            val taskId = (data["taskId"] as? Long ?: 0).toLong()

                            val retrievedTaskModel = TaskModel(
                                taskId = taskId,
                                message = message,
                                title = title,
                                workerList = workerList,
                                day = day,
                                currentUser = userSnapshot.id, // Güncelleme gerekebilir
                                constructionArea = constructionSiteSnapshot.id, // Güncelleme gerekebilir
                            )
                            tasks.add(retrievedTaskModel)
                        }
                    }
                }
            }
            tasks.toList()
        }
    }

    override suspend fun saveStatisticInfo(workerInfoModel: WorkInfoModel): Result<Unit> {
        return kotlin.runCatching {

            val fileName = UUID.randomUUID()
            val currentUserRef = database.collection("Users").document(workerInfoModel.currentUser)
            val constructionSiteRef = currentUserRef.collection("construcitonName").document(workerInfoModel.constructionArea)
            val postsRef = constructionSiteRef.collection("statistic").document(workerInfoModel.vocation)
            val statisticRef=postsRef.collection("randomId").document(fileName.toString())

            currentUserRef.set(hashMapOf("dummyField" to "dummyValue")).await()
            constructionSiteRef.set(hashMapOf("dummyField" to "dummyValue")).await()
            postsRef.set(hashMapOf("dummyField" to "dummyValue")).await()

            val post = hashMapOf(
                "vocation" to workerInfoModel.vocation,
                "operationTime" to workerInfoModel.operationTime,
                "operationDuration" to workerInfoModel.operationDuration,
                "currentUser" to workerInfoModel.currentUser,
                "constructionArea" to workerInfoModel.constructionArea,
            )

            statisticRef.set(post).await()

        }
    }

   override suspend fun getStatisticForVocation(infoCurrentUser: String, constructionName: String, infoVocation: String): Result<List<WorkInfoModel>> {
        return kotlin.runCatching {
            val statisticList = mutableListOf<WorkInfoModel>()

            val currentUserRef = database.collection("Users").document(infoCurrentUser)
            val constructionSiteRef = currentUserRef.collection("construcitonName").document(constructionName)

            val statisticQuery = constructionSiteRef.collection("statistic")
                .whereEqualTo("vocation", infoVocation)
                .get()
                .await()

            for (statisticDoc in statisticQuery.documents) {
                val data = statisticDoc.data
                val vocation = data?.get("vocation") as? String ?: ""
                val operationTime = data?.get("operationTime") as? String ?: ""
                val operationDuration = data?.get("operationDuration") as? Int ?: 0
                val currentUser = data?.get("currentUser") as? String ?: ""
                val constructionArea = data?.get("constructionArea") as? String ?: ""

                val workInfo = WorkInfoModel(vocation, operationTime, operationDuration, currentUser, constructionArea)
                statisticList.add(workInfo)
            }

            statisticList
        }
    }




}