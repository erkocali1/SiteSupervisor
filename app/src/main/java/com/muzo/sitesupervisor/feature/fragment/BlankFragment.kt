package com.muzo.sitesupervisor.feature.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.dhaval2404.imagepicker.ImagePicker
import com.muzo.sitesupervisor.databinding.FragmentBlankBinding


class BlankFragment : Fragment() {
    private lateinit var binding:FragmentBlankBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentBlankBinding.inflate(layoutInflater,container,false)

        return binding.root

    }


}