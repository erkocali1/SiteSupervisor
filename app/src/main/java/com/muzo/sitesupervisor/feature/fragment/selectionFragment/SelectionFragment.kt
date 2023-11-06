package com.muzo.sitesupervisor.feature.fragment.selectionFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.databinding.FragmentSelectionBinding


class SelectionFragment : Fragment() {
    private lateinit var binding: FragmentSelectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectionBinding.inflate(layoutInflater, container, false)


        val items= listOf("hello","karemel")
        val adapter=ArrayAdapter(requireContext(), R.layout.list_item,items)



        return binding.root
    }


}