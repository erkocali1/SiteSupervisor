package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class ChangePasswordUseCase @Inject constructor(private val repository: FireBaseRepository) {

    suspend  operator  fun invoke(itemValue: String, siteSuperVisor: String, constructionName: String): Flow<Unit> {
        return flow {

            val result=repository.changeSitePassword(itemValue, siteSuperVisor, constructionName)
            (result.getOrNull() ?: throw  IllegalArgumentException(Constants.ERROR_MESSAGE)).also { emit(it) }
        }

    }
}