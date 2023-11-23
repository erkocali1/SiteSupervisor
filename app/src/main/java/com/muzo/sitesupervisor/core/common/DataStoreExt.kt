package com.muzo.sitesupervisor.core.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


    const val PREFS_NAME = "data_pref"
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)

