package com.muzo.sitesupervisor.domain

import com.muzo.sitesupervisor.core.constans.Constants.Companion.ERROR_MESSAGE
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel
import com.muzo.sitesupervisor.core.data.remote.repository.fireBaseData.FireBaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetStatisticForVocationUseCase @Inject constructor(
    private val repository: FireBaseRepository
) {

    suspend operator fun invoke(infoCurrentUser: String, constructionName: String, infoVocation: String):
            Flow<List<WorkInfoModel>> {

        return flow {
            val result=repository.getStatisticForVocation(infoCurrentUser, constructionName, infoVocation)
            (result.getOrNull()?:throw IllegalArgumentException(ERROR_MESSAGE)).also { emit(it) }
        }

    }
}