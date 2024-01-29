package com.muzo.sitesupervisor.core.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkInfoModel(
    val vocation: String,
    val operationTime: String,
    val operationDuration: Long,
    val currentUser: String,
    val constructionArea: String,
    val specifiedMonth:String,
    val cost:String,
    val amountPaid:String,
    val randomId:String?=null
) : Parcelable