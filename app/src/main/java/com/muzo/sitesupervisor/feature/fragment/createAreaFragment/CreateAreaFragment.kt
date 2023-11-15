package com.muzo.sitesupervisor.feature.fragment.createAreaFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.databinding.FragmentCreateAreaBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAreaFragment : Fragment() {
    private lateinit var binding: FragmentCreateAreaBinding
    private val viewModel: CreateAreFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAreaBinding.inflate(layoutInflater, container, false)

        observeData()
        return binding.root
    }

    private fun observeData() {

        binding.btnCreate.setOnClickListener {

            val constructionName = binding.etConstructionName.text.toString()

            postFirstEntity()

            if (constructionName.isNotEmpty()) {
                viewModel.saveArea(postFirstEntity())

                lifecycleScope.launch {
                    viewModel.uiState.collect { uiState ->
                        when {

                            uiState.loading -> {
                                binding.progressBar.show()
                            }

                            uiState.isSuccessful -> {
                                toastMessage(uiState.message!!)
                                navigateFragment()
                            }

                            uiState.message != null -> {
                                toastMessage(uiState.message.toString())
                                binding.progressBar.hide()
                            }
                        }
                    }
                }
            } else {
                toastMessage("please feel the Information")
            }
        }
    }

    private fun toastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateFragment() {
        findNavController().navigate(R.id.action_createAreaFragment_to_listingFragment)
    }

    private fun postFirstEntity(): DataModel {
        val constructionName = binding.etConstructionName.text.toString()
        val currentUser = viewModel.currentUser?.uid.toString()

        return DataModel(
            message = "Hadi kaydetmeye başlayalım",
            title = "Şantiye defteri oluşturuldu",
            photoUrl = "",
            timestamp = 1234,
            currentUser = currentUser,
            constructionArea = constructionName

        )
    }

}