package com.muzo.sitesupervisor.domain

import android.net.Uri
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddImageUrlToFireStoreUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend operator fun invoke(downloadUrl:List<Uri>, currentUser: String, constructionName: String, postId: String): Flow<Unit> {
        return flow {
            val result = repository.addImageUrlToFireStore(downloadUrl, currentUser, constructionName, postId)
            (result.getOrNull() ?: throw IllegalArgumentException(Constants.ERROR_MESSAGE)).also {
                emit(it)
            }
        }
    }
}