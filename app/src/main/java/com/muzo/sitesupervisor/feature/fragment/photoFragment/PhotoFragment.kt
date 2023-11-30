package com.muzo.sitesupervisor.feature.fragment.photoFragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R

import com.muzo.sitesupervisor.databinding.FragmentPhotoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class PhotoFragment : Fragment() {
    private lateinit var binding:FragmentPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentPhotoBinding.inflate(layoutInflater,container,false)
        turnBackFragment()
        val uri=arguments?.getString("bigPhoto")

        uri?.let {
            binding.mBigImage.showImage(Uri.parse(uri))
        }

      return  binding.root
    }

    private fun turnBackFragment() {
        binding.back.setOnClickListener {
           findNavController().navigate(R.id.action_photoFragment_to_detailFragment)

        }
    }

}