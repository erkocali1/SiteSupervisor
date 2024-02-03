package com.muzo.sitesupervisor.feature.fragment.selectionFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.databinding.FragmentSelectionBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SelectionFragment : Fragment() {
    private lateinit var binding: FragmentSelectionBinding
    private val viewModels:SelectionFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectionBinding.inflate(layoutInflater, container, false)

        setButtonClickListeners()
        backPressEvent()
        return binding.root

    }

    private fun navigateFragment(destinationId: Int) {
        findNavController().navigate(destinationId)
    }

    private fun setButtonClickListeners() {
        binding.btnJoin.setOnClickListener { navigateFragment(R.id.action_selectionFragment_to_joinAreaFragment) }
        binding.btnCreate.setOnClickListener { navigateFragment(R.id.action_selectionFragment_to_createAreaFragment) }
    }


    private fun backPressEvent(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            requireActivity().finish()
        }
    }





}