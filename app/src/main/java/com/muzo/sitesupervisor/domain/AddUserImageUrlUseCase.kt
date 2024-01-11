package com.muzo.sitesupervisor.domain

import android.net.Uri
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserImageUrlUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator fun invoke(downloadUrl: Uri, currentUser: String, constructionName: String): Flow<Unit>{
        return flow {
            val result=repository.addImageUrlToFireStore(downloadUrl, currentUser, constructionName)
            (result.getOrNull() ?: throw  IllegalArgumentException(Constants.ERROR_MESSAGE)).also { emit(it) }
        }
    }
}