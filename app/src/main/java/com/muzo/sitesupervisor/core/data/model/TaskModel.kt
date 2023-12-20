package com.muzo.sitesupervisor.core.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "task"
)
data class TaskModel(
    @PrimaryKey(autoGenerate = true) var taskId: Long? = null,
    var message: String,
    var title: String,
    var day: String,
    var workerList: List<String>?=null,
    var currentUser: String,
    var constructionArea: String,
    ) : Parcelable
