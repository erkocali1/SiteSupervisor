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
    val message: String,
    val title: String,
    var photoUrl: List<String>?=null,
    val day: String,
    val time: String,
    val currentUser: String,
    val constructionArea: String

) : Parcelable



