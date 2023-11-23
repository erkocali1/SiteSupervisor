package com.muzo.sitesupervisor.core.data.local.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// ...

class MyDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun saveData(
        userKey: String,
        constructionKey: String,
        currentUser: String,
        constructionName: String
    ) {
        val userPrefsKey = stringPreferencesKey(userKey)
        val constructionPrefsKey = stringPreferencesKey(constructionKey)

        dataStore.edit { prefs ->
            prefs[userPrefsKey] = currentUser
            prefs[constructionPrefsKey] = constructionName
        }
    }

    suspend fun readDataStore(userKey: String): String? {
        val dataStoreKey = stringPreferencesKey(userKey)
        return dataStore.data.map { prefs ->
            prefs[dataStoreKey] ?: ""
        }.first()
    }


}
