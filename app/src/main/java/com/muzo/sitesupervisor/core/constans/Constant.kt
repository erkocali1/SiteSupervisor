package com.muzo.sitesupervisor.core.constans

class Constants {

    companion object {

        const val REQUEST_CODE_LOCATION_PERMISSION = 0

        const val ERROR_MESSAGE = "Registration failed"
        const val OK_MESSAGE = "Your application is okey"
        const val DATABASE_NAME = "news_db"
        const val PREF_NAME = "login_event"
        const val KEY_IS_ENTERED = "isEntered"

        object ConstructionTeams {
            val TEAMS = listOf(
                "Demirci",
                "Kalıpçılar",
                "Duvarcılar",
                "Duvar Ustaları",
                "Elektrikçiler",
                "Tesisatçılar",
                "Mobilyacı",
                "Çatı Ustaları",
                "İzolasyon Ekibi",
                "Boyacılar",
                "Pvc Ekibi",
                "Seramik Ekibi",
                "Mermer Ekibi"
            )
        }

    }
}