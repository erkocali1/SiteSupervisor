package com.muzo.sitesupervisor.feature.fragment.createAreaFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.databinding.FragmentCreateAreaBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAreaFragment : Fragment() {
    private lateinit var binding:FragmentCreateAreaBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCreateAreaBinding.inflate(layoutInflater,container,false)


        return binding.root
    }


}