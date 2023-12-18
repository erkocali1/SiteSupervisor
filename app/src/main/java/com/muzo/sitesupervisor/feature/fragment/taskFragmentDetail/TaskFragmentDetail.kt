package com.muzo.sitesupervisor.feature.fragment.taskFragmentDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.constans.Constants.Companion.ConstructionTeams.TEAMS
import com.muzo.sitesupervisor.databinding.FragmentTaskDetailBinding
import com.muzo.sitesupervisor.feature.adapters.textAdapter.TextAdapter
import com.muzo.sitesupervisor.feature.fragment.registerfragment.RegisterFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint

class TaskFragmentDetail : Fragment() {
    private lateinit var binding: FragmentTaskDetailBinding
    private val viewModel: TaskFragmentDetailViewModel by viewModels()
    private var stringList: MutableList<String>? = mutableListOf()
    private lateinit var adapter: TextAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTaskDetailBinding.inflate(layoutInflater, container, false)

        setList()

        return binding.root
    }

    private fun setList() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, TEAMS)
        binding.listConstruction.setAdapter(adapter)

        binding.listConstruction.setOnItemClickListener { _, _, position, _ ->
            val selectedConstruction = TEAMS[position]
            val selectedList = listOf(selectedConstruction)
            stringList?.addAll(selectedList)
            setupAdapter()
            toastMessage(
                "$selectedConstruction listeye eklendi", requireContext()
            )
        }
    }


    private fun setupAdapter() {
        adapter = TextAdapter(stringList) {
            deleteFromList(it)

        }
        binding.rvWorkerPicker.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvWorkerPicker.adapter = adapter

    }

    private fun deleteFromList(item: String) {
        stringList?.remove(item)
        toastMessage("$item kaldırıldı", requireContext())
        adapter.notifyDataSetChanged()
    }

}