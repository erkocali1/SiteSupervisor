package com.muzo.sitesupervisor.core.data.model

import android.icu.text.CaseMap.Title
import java.sql.Timestamp


data class DataModel(
    val message:String="Önemli buldugunuz bilgileri kaydedin",
    val title: String="Şantiye defteri oluşturuldu",
    val photoUrl:String="",
    val timestamp:Number=12341,
    val currentUser:String=""

)
