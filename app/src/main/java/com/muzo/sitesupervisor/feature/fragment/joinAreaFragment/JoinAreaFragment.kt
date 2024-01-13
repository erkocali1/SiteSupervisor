package com.muzo.sitesupervisor.feature.fragment.joinAreaFragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.databinding.FragmentJoinAreaBinding
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.aestheticdialogs.OnDialogClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JoinAreaFragment : Fragment() {
    private lateinit var binding: FragmentJoinAreaBinding
    private val viewModel: JoinFragmentViewModel by viewModels()
    private lateinit var list: List<UserConstructionData>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentJoinAreaBinding.inflate(layoutInflater, container, false)

        infAlert()
        observeData()

        return binding.root
    }

    private fun observeData() {

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->

                when {
                    uiState.loading -> {
                        binding.progressBar.show()

                    }

                    uiState.resultList != null -> {
                        binding.progressBar.hide()
                        list = uiState.resultList
                        setList(getConstructionNames(list))
                    }
                }
            }
        }
    }

    private fun infAlert() {
        AestheticDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.INFO)
            .setTitle("Select the construction site you want to Investigate").setCancelable(false)
            .setMessage("").setDarkMode(false).setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.DEFAULT)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                    //actions...
                }
            }).show()
    }

    private fun toastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun getConstructionNames(data: List<UserConstructionData>): List<String> {
        // Sadece şantiye isimlerini alarak bir liste oluştur
        val constructionNames = mutableListOf<String>()
        for (userConstructionData in data) {
            constructionNames.addAll(userConstructionData.constructionAreas)
        }
        return constructionNames
    }

    private fun setList(constructionNames: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, constructionNames)
        binding.listConstruction.setAdapter(adapter)

        binding.listConstruction.setOnItemClickListener { _, _, position, _ ->
            val selectedConstruction = constructionNames[position]
            toastMessage("You selected the $selectedConstruction construction name")
            joinEvent(selectedConstruction)
        }
    }

    private fun joinEvent(constructionName: String) {

        val selectedUserConstructionData = list.find { userConstructionData ->
            userConstructionData.constructionAreas.contains(constructionName)
        }

        val currentUser = selectedUserConstructionData?.currentUser ?: "Default Current User"

        val userConstructionData = UserConstructionData(currentUser, listOf(constructionName))


        lifecycleScope.launch {
            viewModel.saveDataStore(currentUser, constructionName)
            binding.btnJoin.setOnClickListener {
                Log.d("kontrol", userConstructionData.currentUser)
                findNavController().navigate(R.id.action_joinAreaFragment_to_verifyPasswordFragment)
            }
        }
    }

}