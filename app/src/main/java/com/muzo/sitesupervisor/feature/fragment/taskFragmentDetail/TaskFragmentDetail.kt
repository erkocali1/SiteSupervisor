package com.muzo.sitesupervisor.feature.fragment.taskFragmentDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.constans.Constants.Companion.ConstructionTeams.TEAMS
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.databinding.FragmentTaskDetailBinding
import com.muzo.sitesupervisor.feature.adapters.textAdapter.TextAdapter
import com.muzo.sitesupervisor.feature.fragment.registerfragment.RegisterFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint

class TaskFragmentDetail : Fragment() {
    private lateinit var binding: FragmentTaskDetailBinding
    private val viewModel: TaskFragmentDetailViewModel by viewModels()
    private var stringList: MutableList<String>? = mutableListOf()
    private lateinit var adapter: TextAdapter
    private var postId: Long? = null
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private var dataEmpty: Boolean = false
    private var sendData: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTaskDetailBinding.inflate(layoutInflater, container, false)

        getSiteInfo()
        setList()
        addItemFromEditText()
        sendData()

        return binding.root
    }

    private fun observeData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when {
                        uiState.loading -> {
                            binding.progressBar.show()
                        }

                        uiState.isSaveTask && sendData -> {
                            binding.progressBar.hide()
                            navigateFragment()
                        }
                    }
                }

            }
        }

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

    private fun addItemFromEditText() {

        binding.imageButton.setOnClickListener {
            val workerString = binding.etWorker.text.toString()
            if (workerString != "") {
                val workerStringList = listOf(workerString)
                stringList?.addAll(workerStringList)
                setupAdapter()
                binding.etWorker.text?.clear()
            } else {
                toastMessage("Lütfen Geçerli Bir Değer Girin", requireContext())
            }

        }
    }

    private fun getData(): TaskModel? {
        val title = binding.etTitle.text.toString()
        val desc = binding.etWorker.text.toString()
        val workerList = stringList
        val date = binding.time.toString()

        return if (title.isNotEmpty() && desc.isNotEmpty()) {
            TaskModel(postId, desc, title, date, workerList, siteSupervisor, constructionArea)
        } else {
            dataEmpty = true
            null
        }
    }


    private fun sendData() {
        binding.ivButtonOK.setOnClickListener {
            if (dataEmpty) {
                toastMessage("Lütfen bilgilerinizi Doldurunuz", requireContext())
            } else {
                getData()
                lifecycleScope.launch {
                    val postId = viewModel.saveRoom(getData()!!)
                    getData()?.taskId = postId
                    viewModel.saveTask(getData()!!)
                    sendData = true
                    observeData()
                }
            }
        }
    }

    private fun getSiteInfo() {
        lifecycleScope.launch {
            viewModel.readDataStore("construction_key").collect { area ->
                constructionArea = area!!
                viewModel.readDataStore("user_key").collect { supervisor ->
                    siteSupervisor = supervisor!!
                }
            }
        }

    }

    private fun navigateFragment() {
        findNavController().navigate(R.id.action_taskFragment_to_taskFragmentDetail)
    }

}