package com.muzo.sitesupervisor.feature.fragment.listingNotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.databinding.FragmentListingBinding


class ListingFragment : Fragment() {
    private lateinit var binding: FragmentListingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentListingBinding.inflate(layoutInflater,container,false)


        binding.fabBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listingFragment_to_detailFragment)

        }


        return binding.root
    }
}