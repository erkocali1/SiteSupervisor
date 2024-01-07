package com.muzo.sitesupervisor.core.data.model

data class WorkInfoModel(
    val vocation: String,
    val operationTime: String,
    val operationDuration: Long,
    val currentUser: String,
    val constructionArea: String,
    val specifiedMonth:String,
    val cost:String,
    val amountPaid:String,
)