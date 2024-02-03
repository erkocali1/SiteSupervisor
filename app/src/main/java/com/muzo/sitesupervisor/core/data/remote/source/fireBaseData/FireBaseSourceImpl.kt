package com.muzo.sitesupervisor.core.data.remote.source.fireBaseData

import android.net.Uri
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.muzo.sitesupervisor.core.common.await
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.core.data.model.UserInfo
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

    override suspend fun deletePost(
        currentUser: String,
        constructionName: String,
        postId: String
    ): Result<Unit> {
        return kotlin.runCatching {
            database.collection("Users").document(currentUser).collection("construcitonName")
                .document(constructionName).collection("posts").document(postId).delete().await()
        }
    }


   override suspend fun fetchData(
        currentUser: String,
        constructionName: String,
        postId: String
    ): Result<DataModel> {
        return kotlin.runCatching {
            val querySnapshot =
                database.collection("Users")
                    .document(currentUser)
                    .collection("construcitonName")
                    .document(constructionName)
                    .collection("posts")
                    .orderBy("postId")
                    .get()
                    .await()

            val dataModels = mutableListOf<DataModel>()

            for (document in querySnapshot.documents) {
                val data = document.data
                val message = data?.get("message") as? String ?: ""
                val title = data?.get("title") as? String ?: ""
                val photoUrl = data?.get("photoUrl") as? List<String> ?: listOf()
                val day = data?.get("day") as? String ?: ""
                val time = data?.get("time") as? String ?: ""
                val id = data?.get("postId") as? Long ?: 0
                val modificationDate = data?.get("modificationDate") as? String ?: ""
                val modificationTime = data?.get("modificationTime") as? String ?: ""

                dataModels.add(
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
                )
            }

            // Assuming you want to return the first element
            dataModels.firstOrNull() ?: throw NoSuchElementException("Document not found")
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
        fileUris: List<Uri>?,
        postId: String
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

    override suspend fun deleteTask(
        currentUser: String,
        constructionName: String,
        date: String,
        taskId: String
    ): Result<Unit> {
        return kotlin.runCatching {
            // Delete the specific task
            database.collection("Users").document(currentUser)
                .collection("construcitonName").document(constructionName)
                .collection("task").document(date)
                .collection("taskId").document(taskId).delete()

            // Check if there are any remaining tasks under the date
            val dateCollectionRef = database.collection("Users").document(currentUser)
                .collection("construcitonName").document(constructionName)
                .collection("task").document(date).collection("taskId").get().await()

            // If no remaining tasks, delete the entire date document
            if (dateCollectionRef.isEmpty) {
                database.collection("Users").document(currentUser)
                    .collection("construcitonName").document(constructionName)
                    .collection("task").document(date).delete()
            }
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


    override suspend fun getTasksWithWorker(
        siteSuperVisor: String,
        constructionName: String,
        workerName: String
    ): Result<List<TaskModel>> {
        return kotlin.runCatching {
            val tasks = mutableListOf<TaskModel>()

            val userRef = database.collection("Users").document(siteSuperVisor)
            val constructionSiteRef = userRef.collection("construcitonName").document(constructionName)
            val taskCollection = constructionSiteRef.collection("task")

            val taskQuerySnapshot = taskCollection
                .get()
                .await()

            for (taskDoc in taskQuerySnapshot) {
                val taskIdCollectionRef = taskDoc.reference.collection("taskId")
                val taskIdQuerySnapshot = taskIdCollectionRef.get().await()

                for (taskIdDoc in taskIdQuerySnapshot) {
                    val data = taskIdDoc.data
                    val workerList = data["workerList"] as? List<String> ?: listOf()

                    if (workerList.contains(workerName)) {
                        val message = data["message"] as? String ?: ""
                        val title = data["title"] as? String ?: ""
                        val day = data["day"] as? String ?: ""
                        val taskId = (data["taskId"] as? Long ?: 0).toLong()

                        val retrievedTaskModel = TaskModel(
                            taskId = taskId,
                            message = message,
                            title = title,
                            workerList = workerList,
                            day = day,
                            currentUser = siteSuperVisor,
                            constructionArea = constructionName
                        )
                        tasks.add(retrievedTaskModel)
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
            val constructionSiteRef = currentUserRef.collection("construcitonName")
                .document(workerInfoModel.constructionArea)
            val postsRef =
                constructionSiteRef.collection("statistic").document(workerInfoModel.vocation)
            val statisticRef = postsRef.collection("randomId").document(fileName.toString())

            currentUserRef.set(hashMapOf("dummyField" to "dummyValue")).await()
            constructionSiteRef.set(hashMapOf("dummyField" to "dummyValue")).await()
            postsRef.set(hashMapOf("dummyField" to "dummyValue")).await()

            val post = hashMapOf(
                "vocation" to workerInfoModel.vocation,
                "operationTime" to workerInfoModel.operationTime,
                "operationDuration" to workerInfoModel.operationDuration,
                "currentUser" to workerInfoModel.currentUser,
                "constructionArea" to workerInfoModel.constructionArea,
                "specifiedMonth" to workerInfoModel.specifiedMonth,
                "cost" to workerInfoModel.cost,
                "amountPaid" to workerInfoModel.amountPaid,
                "randomId" to fileName.toString()
            )

            statisticRef.set(post).await()

        }
    }

    override suspend fun deleteStatisticWithRandomId(
        siteSuperVisor: String,
        constructionName: String,
        infoVocation: String,
        randomId: String
    ): Result<Unit> {
        return kotlin.runCatching {
            val currentUserRef = database.collection("Users").document(siteSuperVisor)
            val constructionSiteRef =
                currentUserRef.collection("construcitonName").document(constructionName)
            val postsRef = constructionSiteRef.collection("statistic").document(infoVocation)
            val statisticSnapshot = postsRef.collection("randomId").get().await()

            for (document in statisticSnapshot.documents) {
                postsRef.collection("randomId").document(randomId).delete().await()
            }

        }
    }


    override suspend fun getStatisticForVocation(
        infoCurrentUser: String,
        constructionName: String,
        infoVocation: String
    ): Result<List<WorkInfoModel>> {
        return kotlin.runCatching {
            val currentUserRef = database.collection("Users").document(infoCurrentUser)
            val constructionSiteRef =
                currentUserRef.collection("construcitonName").document(constructionName)
            val postsRef = constructionSiteRef.collection("statistic").document(infoVocation)
            val statisticSnapshot = postsRef.collection("randomId").get().await()

            val workInfoModels = mutableListOf<WorkInfoModel>()

            for (document in statisticSnapshot.documents) {
                val data = document.data
                val operationTime = data?.get("operationTime") as? String ?: ""
                val operationDuration = data?.get("operationDuration") as? Long ?: 0
                val currentUser = data?.get("currentUser") as? String ?: ""
                val constructionArea = data?.get("constructionArea") as? String ?: ""
                val specifiedMonth = data?.get("specifiedMonth") as? String ?: ""
                val cost = data?.get("cost") as? String ?: ""
                val amountPaid = data?.get("amountPaid") as? String ?: ""
                val randomId = data?.get("randomId") as? String ?: ""

                val workInfoModel = WorkInfoModel(
                    vocation = infoVocation,
                    operationTime = operationTime,
                    operationDuration = operationDuration,
                    currentUser = currentUser,
                    constructionArea = constructionArea,
                    specifiedMonth = specifiedMonth,
                    cost = cost,
                    amountPaid = amountPaid,
                    randomId = randomId,
                )
                workInfoModels.add(workInfoModel)
            }

            workInfoModels

        }
    }

    //-----------------Location//-----------------\\


    override suspend fun saveLocation(
        latLng: LatLng,
        currentUser: String,
        constructionName: String
    ): Result<Unit> {

        return kotlin.runCatching {
            val currentUserRef = database.collection("Users").document(currentUser)
            val constructionSiteRef =
                currentUserRef.collection("construcitonName").document(constructionName)
            val locationRef = constructionSiteRef.collection("location").document(constructionName)

            val newLocation = hashMapOf(
                "latitude" to latLng.latitude.toString(),
                "longitude" to latLng.longitude.toString(),
            )

            locationRef.set(newLocation)


        }
    }

    override suspend fun uploadLocation(
        currentUser: String,
        constructionName: String
    ): Result<Pair<String, String>> {
        return kotlin.runCatching {
            val currentUserRef = database.collection("Users").document(currentUser)
            val constructionSiteRef =
                currentUserRef.collection("construcitonName").document(constructionName)
            val locationRef =
                constructionSiteRef.collection("location").document(constructionName).get().await()

            val data = locationRef.data

            val latitude = data?.get("latitude") as String? ?: ""
            val longitude = data?.get("longitude") as String? ?: ""

            Pair(latitude, longitude)
        }
    }

    override suspend fun addUserImage(fileUri: Uri, siteSuperVisor: String): Result<Uri> {
        return kotlin.runCatching {
            var uploadedUri: Uri? = null // uploadedUri'yi null olarak başlatın

            val fileRef =
                storage.reference.child("users").child("siteSuperVisor").child(siteSuperVisor)

            // putFile fonksiyonunu kullanarak dosyayı yükleyin
            fileRef.putFile(fileUri).await()

            // downloadUrl fonksiyonunu kullanarak yüklenen dosyanın indirme bağlantısını alın
            val downloadUrl = fileRef.downloadUrl.await()

            uploadedUri = Uri.parse(downloadUrl.toString())

            uploadedUri
        }
    }


    override suspend fun changeUserItem(
        itemValue: String,
        currentUser: String,
        changedItem: String
    ): Result<Unit> {
        return kotlin.runCatching {
            val currentUserRef = database.collection("UsersInfo").document(currentUser)

            val data = hashMapOf(changedItem to itemValue)
            currentUserRef.update(data.toMap()).await()
        }
    }

    override suspend fun changeSitePassword(
        itemValue: String,
        siteSuperVisor: String,
        constructionName: String
    ): Result<Unit> {
        return kotlin.runCatching {
            val currentUserRef =
                database.collection("UsersInfo").document(siteSuperVisor).collection("constName")
                    .document(constructionName)

            val data = hashMapOf("Password" to itemValue)
            currentUserRef.update(data.toMap()).await()
        }
    }

    override suspend fun getSitePassword(
        siteSuperVisor: String,
        constructionName: String
    ): Result<String> {
        return kotlin.runCatching {
            val currentUserRef =
                database.collection("UsersInfo").document(siteSuperVisor).collection("constName")
                    .document(constructionName)
            val documentSnapshot = currentUserRef.get().await()

            val data = documentSnapshot.data
            val password = data?.get("Password") as String
            password
        }
    }

    override suspend fun addTeam(
        currentUser: String,
        teams: List<String>,
        constructionName: String
    ): Result<Unit> {
        return kotlin.runCatching {
            val currentUserRef =
                database.collection("UsersInfo").document(currentUser).collection("constName")
                    .document(constructionName)


            val post = hashMapOf(
                "teams" to teams,
            )
            currentUserRef.set(post).await()
        }
    }

    override suspend fun updateTeam(
        currentUser: String,
        teams: List<String>,
        constructionName: String
    ): Result<Unit> {
        return kotlin.runCatching {
            val currentUserRef =
                database.collection("UsersInfo").document(currentUser).collection("constName")
                    .document(constructionName)

            val post = hashMapOf<String, Any>(
                "teams" to teams as Any
            )
            currentUserRef.update(post).await()
        }
    }

    override suspend fun getTeam(
        currentUser: String,
        constructionName: String
    ): Result<List<String>> {
        return kotlin.runCatching {
            val currentUserRef =
                database.collection("UsersInfo").document(currentUser).collection("constName")
                    .document(constructionName).get().await()

            val data = currentUserRef.data
            val teams = data?.get("teams") as? List<String> ?: emptyList()
            teams
        }
    }


    override suspend fun addUserInfo(currentUser: String, userInfo: UserInfo): Result<Unit> {
        return kotlin.runCatching {
            val currentUserRef = database.collection("UsersInfo").document(currentUser)

            val post = hashMapOf(
                "name" to userInfo.name,
                "email" to userInfo.email,
                "photoUrl" to userInfo.photoUrl,
                "phoneNumber" to userInfo.phoneNumber,
            )
            currentUserRef.set(post).await()
        }
    }

    override suspend fun getSiteSuperVisorInfo(siteSuperVisor: String): Result<UserInfo> {
        return kotlin.runCatching {
            val currentUserRef =
                database.collection("UsersInfo").document(siteSuperVisor).get().await()

            val data = currentUserRef.data

            val mail = data?.get("email") as String? ?: ""
            val name = data?.get("name") as String? ?: ""
            val phone = data?.get("phoneNumber") as String? ?: ""
            val photoUrl = data?.get("photoUrl") as String? ?: ""

            val userInfo = UserInfo(
                email = mail,
                name = name,
                phoneNumber = phone,
                photoUrl = photoUrl,
            )
            userInfo
        }
    }
}
