package com.muzo.sitesupervisor.feature.fragment.settingsFragment

import androidx.lifecycle.ViewModel
import com.muzo.sitesupervisor.core.data.local.dataStore.MyDataStore
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SettingsFragmentViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val dataStore: MyDataStore
) : ViewModel() {

    val currentUser = authRepository.currentUser?.uid.toString()
    fun readDataStore(userKey: String): Flow<String?> {
        return dataStore.readDataStore(userKey)
    }

}