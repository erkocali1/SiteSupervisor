package com.muzo.sitesupervisor.core.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "post"
)

data class DataModel(
    @PrimaryKey(autoGenerate = true) var id: Long?=null,
    var message: String,
    var title: String,
    var photoUrl: List<String>?=null,
    var day: String,
    var time: String,
    var currentUser: String,
    var constructionArea: String,
    var modificationDate:String?=null,
    var modificationTime:String?=null,

    ) : Parcelable



