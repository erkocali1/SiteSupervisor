package com.muzo.sitesupervisor.feature.fragment.detailFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val viewModel: DetailFragmentViewModel by viewModels()
    private lateinit var binding: FragmentDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)

        showDayAndTime()
        clickListener()
        observeData()
        return binding.root
    }

    private fun showDayAndTime() {
        val (currentDate, currentTime) = viewModel.getCurrentDateAndTime()
        binding.textDay.text = currentDate
        binding.textTime.text = currentTime
    }

    private fun takeData(): DataModel {
        val title = binding.etTitle.text.toString()
        val message = binding.etDes.text.toString()
        val photoUrl = ""
        val (day, time) = viewModel.getCurrentDateAndTime()
        val currentUser = viewModel.currentUser
        val constructionName = arguments?.getString("constructionName")

        return DataModel(message, title, photoUrl, day, time, currentUser, constructionName!!)

    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()
                    }

                    uiState.isSuccessful -> {
                        toastMessage(uiState.message!!)
                    }
                }
            }
        }
    }

    private fun updateEvent() {
        val dataModel = takeData()
        lifecycleScope.launch {
            viewModel.updateData(dataModel)
        }
    }
    private fun clickListener() {

        binding.okBtn.setOnClickListener {
            if (isAllFieldsFilled()) {
                updateEvent()
            } else {
                toastMessage("Lütfen tüm bilgileri düzgün doldurunuz")
            }

        }
    }

    private fun isAllFieldsFilled(): Boolean {
        val description = binding.etDes.text.toString()
        val day = binding.textDay.text.toString()
        val title = binding.etTitle.text.toString()

        return description.isNotBlank() && day.isNotBlank() && title.isNotBlank()
    }

    private fun toastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}