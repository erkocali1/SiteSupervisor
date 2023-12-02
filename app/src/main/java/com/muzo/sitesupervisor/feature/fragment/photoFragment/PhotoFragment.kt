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
import com.muzo.sitesupervisor.databinding.FragmentPhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint

class PhotoFragment : Fragment() {
    private lateinit var binding: FragmentPhotoBinding
    private val viewModel: PhotoFragmentViewModel by viewModels()
    private  var postId: Long? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPhotoBinding.inflate(layoutInflater, container, false)
        turnBackFragment()
        val uri = arguments?.getString("bigPhoto")
        postId = arguments?.getLong("id")

        deleteThisPhoto(postId!!, uri!!)
        binding.mBigImage.showImage(Uri.parse(uri))

        return binding.root
    }

    private fun turnBackFragment() {
        binding.back.setOnClickListener {
            val receivedData = arguments?.getLong("id")
            val bundle=Bundle().apply {
                putString("from", "recyclerview")
                putLong("id",receivedData!!)
            }

            findNavController().navigate(R.id.action_photoFragment_to_detailFragment,bundle)

        }
    }

    private fun deleteThisPhoto(postId: Long, urlToDelete: String) {
        binding.deleteIc.setOnClickListener {
            val receivedData = arguments?.getLong("id")
            val bundle=Bundle().apply {
                putString("from", "recyclerview")
                putLong("id",receivedData!!)
            }
            lifecycleScope.launch {
                viewModel.deletePhotoUrl(postId, urlToDelete)
                findNavController().navigate(R.id.action_photoFragment_to_detailFragment,bundle)
            }
        }
    }

}