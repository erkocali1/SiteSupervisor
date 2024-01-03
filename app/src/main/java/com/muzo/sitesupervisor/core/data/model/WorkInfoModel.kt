package com.muzo.sitesupervisor.core.data.model

data class WorkInfoModel(
    val vocation: String,
    val operationTime: String,
    val operationDuration: Int,
    var currentUser: String,
    var constructionArea: String
)