package com.muzo.sitesupervisor.domain

import android.net.Uri
import com.muzo.sitesupervisor.core.constans.Constants.Companion.ERROR_MESSAGE
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserImageUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator fun invoke(fileUri: Uri, siteSuperVisor: String): Flow<Uri>{
        return flow {
            val result=repository.addUserImage(fileUri, siteSuperVisor)
            (result.getOrNull() ?: throw  IllegalArgumentException(ERROR_MESSAGE)).also { emit(it) }
        }

    }
}