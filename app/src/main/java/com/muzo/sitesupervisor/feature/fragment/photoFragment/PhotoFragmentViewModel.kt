package com.muzo.sitesupervisor.feature.fragment.photoFragment

import androidx.lifecycle.ViewModel
import com.muzo.sitesupervisor.core.data.local.repository.LocalPostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class PhotoFragmentViewModel @Inject constructor(private val repository: LocalPostRepository):ViewModel() {



    suspend fun deletePhotoUrl(postId: Long, urlToDelete: String){
        repository.updatePost(postId,urlToDelete)
    }

}