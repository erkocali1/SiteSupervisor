package com.muzo.sitesupervisor.feature.fragment.settingsFragment.team

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.databinding.FragmentTeamBinding
import com.muzo.sitesupervisor.feature.adapters.textAdapter.TextAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TeamFragment : Fragment() {

    private lateinit var binding: FragmentTeamBinding
    private val viewModel: TeamFragmentViewModel by viewModels()
    private lateinit var updateJob: Job
    private lateinit var adapter: TextAdapter
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private lateinit var workerTeamList:List<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTeamBinding.inflate(layoutInflater, container, false)
        getSiteInfo()

        viewModel.getTeam(siteSupervisor,constructionArea)
        observeData("get")
        addItemEvent()

        return binding.root
    }

    private fun observeData(observedState: String) {
        updateJob = lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (observedState) {
                    "change" -> {
                        launch {
                            viewModel.addItemState.collect { addItemState ->
                                when {
                                    addItemState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    addItemState.result -> {
                                        binding.progressBar.hide()
                                        toastMessage("İşlem Başarılı Bir Şekilde Gerçekleşti", requireContext())
                                        updateJob.cancel()
                                    }
                                }
                            }
                        }
                    }

                    "get" -> {
                        launch {
                            viewModel.getTeamState.collect { getTeamState ->
                                when {
                                    getTeamState.loading -> {

                                    }

                                    getTeamState.resultList != null -> {
                                        workerTeamList=getTeamState.resultList
                                        setupAdapter(workerTeamList)
                                        updateJob.cancel()
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
    private fun addItemEvent(){
        binding.icAddTeam.setOnClickListener {
            val item= binding.etTeamName.text.toString()
            if (item.isNotEmpty()){
                if (item.length<20){
                    val list=listOf(item)+workerTeamList
                    workerTeamList=list
                    setupAdapter(workerTeamList)

                    viewModel.updateItem(siteSupervisor,list,constructionArea)
                    observeData("change")
                }else{
                    toastMessage("Karakter Sayısı 20 den fazla Olamaz",requireContext())
                }
            }
            else{
                toastMessage("Boş Karaktere İzin Verilmez",requireContext())
            }
        }
    }


    private fun setupAdapter(list: List<String>) {
        adapter = TextAdapter(list) { deletedItem ->
            showDeleteConfirmationDialog(deletedItem)
        }
        binding.rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rv.adapter = adapter
    }

    private fun showDeleteConfirmationDialog(deletedItem: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Bu Ekibi Sil")
        alertDialogBuilder.setMessage("Bu ekip silindiğinde ekibe ait tüm veriler silinicektir.Onaylıyor musunuz?")
        alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
            deleteItem(deletedItem)
        }
        alertDialogBuilder.setNegativeButton("Hayır") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteItem(deletedItem: String) {
        // Tıklanan öğeyi listeden çıkar
        val updatedList = workerTeamList.toMutableList().apply {
            remove(deletedItem)
        }
        workerTeamList = updatedList
        setupAdapter(updatedList)

        viewModel.updateItem(siteSupervisor, updatedList, constructionArea)
        observeData("change")
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
}