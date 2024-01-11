package com.muzo.sitesupervisor.feature.fragment.selectionFragment

import androidx.lifecycle.ViewModel
import com.muzo.sitesupervisor.core.data.remote.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SelectionFragmentViewModel @Inject constructor(private val authRepository: AuthRepository):
    ViewModel() {


    val currentUser = authRepository.currentUser
}