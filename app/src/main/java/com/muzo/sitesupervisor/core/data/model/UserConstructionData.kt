package com.muzo.sitesupervisor.core.data.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class UserConstructionData(
    val currentUser: String="",
    val constructionAreas: List<String>
): Parcelable