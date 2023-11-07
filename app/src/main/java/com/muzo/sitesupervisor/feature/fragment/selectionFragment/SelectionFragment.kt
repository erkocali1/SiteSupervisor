package com.muzo.sitesupervisor.feature.fragment.selectionFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.databinding.FragmentSelectionBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SelectionFragment : Fragment() {
    private lateinit var binding: FragmentSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectionBinding.inflate(layoutInflater, container, false)


//        val items= listOf("hello","karemel")
//        val adapter=ArrayAdapter(requireContext(), R.layout.list_item,items)
        setButtonClickListeners()
        return binding.root


    }

    private fun navigateFragment(destinationId: Int) {
        findNavController().navigate(destinationId)
    }

    private fun setButtonClickListeners() {
        binding.btnJoin.setOnClickListener { navigateFragment(R.id.action_selectionFragment_to_joinAreaFragment) }
        binding.btnCreate.setOnClickListener { navigateFragment(R.id.action_selectionFragment_to_createAreaFragment) }
    }



}