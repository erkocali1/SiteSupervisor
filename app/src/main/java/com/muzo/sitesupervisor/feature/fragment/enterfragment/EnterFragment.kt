package com.muzo.sitesupervisor.feature.fragment.enterfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.muzo.sitesupervisor.databinding.FragmentEnterBinding


class EnterFragment : Fragment() {
    private lateinit var binding: FragmentEnterBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}