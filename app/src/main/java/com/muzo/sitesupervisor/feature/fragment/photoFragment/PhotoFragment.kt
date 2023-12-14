package com.muzo.sitesupervisor.feature.fragment.photoFragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.databinding.FragmentPhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint

class PhotoFragment : Fragment() {
    private lateinit var binding: FragmentPhotoBinding
    private val viewModel: PhotoFragmentViewModel by viewModels()
    private var postId: Long? = null
    private var deleteDataJob: Job? = null
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private var uri: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPhotoBinding.inflate(layoutInflater, container, false)
        turnBackFragment()
        uri = arguments?.getString("bigPhoto")
        postId = arguments?.getLong("id")

        lifecycleScope.launch {
            viewModel.readDataStore("construction_key").collect { area ->
                constructionArea = area!!
                viewModel.readDataStore("user_key").collect { Supervisor ->
                    siteSupervisor = Supervisor!!
                }
            }
        }

        deleteThisPhoto(postId!!, uri!!)
        binding.mBigImage.showImage(Uri.parse(uri))

        return binding.root
    }

    private fun turnBackFragment() {
        binding.back.setOnClickListener {
            sendData()
        }
    }

    private fun deleteThisPhoto(postId: Long, urlToDelete: String) {
        binding.deleteIc.setOnClickListener {
            viewModel.deletePhotoFromFireBase(
                siteSupervisor,
                constructionArea,
                postId.toString(),
                uri!!
            )
            deleteDataJob = lifecycleScope.launch {
                viewModel.deletePhotoUrl(postId, urlToDelete)
                deleteDataJob?.cancel()
                observeData()
            }

        }
    }

    private fun sendData(isDelete: Boolean = false) {
        val receivedData = arguments?.getLong("id")
        val bundle = Bundle().apply {
            putString("from", "recyclerview")
            putLong("id", receivedData!!)
            putBoolean("isDelete", isDelete)

        }
        findNavController().navigate(R.id.action_photoFragment_to_detailFragment, bundle)
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()

                    }

                    uiState.result -> {
                        binding.progressBar.hide()
                        sendData(true)
                    }
                }
            }
        }

    }
}