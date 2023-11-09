package com.muzo.sitesupervisor.feature.fragment.createAreaFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.databinding.FragmentCreateAreaBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAreaFragment : Fragment() {
    private lateinit var binding: FragmentCreateAreaBinding
    private val viewModel: CreateAreFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAreaBinding.inflate(layoutInflater, container, false)

        observeData()
        return binding.root
    }

    private fun observeData() {

        binding.btnCreate.setOnClickListener {
            val constructionName = binding.btnCreate.text.toString()
            val data=DataModel(message = "", collection = constructionName)

            lifecycleScope.launch {
                viewModel._uiState.collect { uiState ->

                    when {
                        uiState.loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        else -> binding.progressBar.visibility = View.GONE

                    }
                    viewModel.save(data)

                }
            }


        }


    }

}