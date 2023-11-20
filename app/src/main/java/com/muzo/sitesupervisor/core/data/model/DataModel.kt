package com.muzo.sitesupervisor.core.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class DataModel(
    val message: String,
    val title: String,
    val photoUrl: String,
    val day: String,
    val time: String,
    val currentUser: String,
    val constructionArea :String

):Parcelable
