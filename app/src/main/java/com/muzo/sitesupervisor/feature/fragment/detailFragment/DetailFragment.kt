package com.muzo.sitesupervisor.feature.fragment.detailFragment

import android.app.Activity
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
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
        showData()
        addPhoto()



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

    private fun showData() {

        val receivedData = arguments?.getParcelable<DataModel>("dataList")
        val title = receivedData?.title
        val message = receivedData?.message
        val photoUrl = receivedData?.photoUrl
        val day = receivedData?.day
        val time = receivedData?.time
        val currentUser = receivedData?.currentUser
        val constructionName = receivedData?.constructionArea

        binding.etTitle.setText(title)
        binding.etDes.setText(message)
        binding.textDay.text = day
        binding.textTime.text = time

    }

    fun addPhoto() {
        binding.cvTimePicker.setOnClickListener {
            ImagePicker.with(requireActivity())
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080, 1080
                ) //Final image resolution will be less than 1080 x 1080(Optional)

                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val imageUri = data?.data ?: return@registerForActivityResult
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            requireContext().contentResolver, imageUri
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                }
                binding.ivButtonOK.setImageURI(imageUri)
                viewModel.uploadData(imageUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                toastMessage("eror")
            } else {
                toastMessage("okey")
            }
        }
}

