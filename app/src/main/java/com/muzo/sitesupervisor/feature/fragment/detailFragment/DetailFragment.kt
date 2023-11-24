package com.muzo.sitesupervisor.feature.fragment.detailFragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.databinding.FragmentDetailBinding
import com.muzo.sitesupervisor.feature.adapters.ImageViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val viewModel: DetailFragmentViewModel by viewModels()
    private lateinit var binding: FragmentDetailBinding
    private lateinit var adapter: ImageViewAdapter
    private val uriList = mutableListOf<Uri>()
    private var from: String? = null
    private var postId: Long? = null
    private lateinit var constructionArea:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)


        lifecycleScope.launch {


            viewModel.readDataStore("construction_key").collect { area ->
                constructionArea = area!!
                Log.d("bura değerlenicek", area)
            }
        }


        getFromLocation()
        showDayAndTime()
        clickListener()
        observeData()
        addPhoto()



        return binding.root
    }

    private fun showDayAndTime() {
        val (currentDate, currentTime) = viewModel.getCurrentDateAndTime()
        binding.textDay.text = currentDate
        binding.textTime.text = currentTime
    }

    private  fun takeData(): DataModel {


        val receivedData = arguments?.getParcelable<DataModel>("dataList")
        val title = binding.etTitle.text.toString()
        val message = binding.etDes.text.toString()
        val photoUrl = "first"
        val (day, time) = viewModel.getCurrentDateAndTime()
        val currentUser = viewModel.currentUser

        val postId = receivedData?.id ?: 0

        return DataModel(
            postId, message, title, photoUrl, day, time, currentUser ?: "", constructionArea
        )
    }




    private fun observeData() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()
                    }

                    uiState.isSuccessful -> {
                        binding.progressBar.hide()
                        toastMessage(uiState.message!!)
                        navigateListingFragment()

                    }
                }
            }
        }
    }

    private  fun saveNewDataEvent() {

           val data = takeData()

        lifecycleScope.launch {
            postId = viewModel.saveRoom(data)
            data.id=postId
            viewModel.addData(data)
            Log.d("bura değerlenicek", postId.toString())
            navigateListingFragment()
        }

    }


    private fun updateEvent() {

        lifecycleScope.launch {
            val dataModel = takeData()
            viewModel.updateData(dataModel)
            //     savePhotoFireBase(this@DetailFragment.uriList)
        }
    }

    private fun clickListener() {

        binding.okBtn.setOnClickListener {
            if (isAllFieldsFilled()) {
                if (getFromLocation()) {
                    saveNewDataEvent()
                } else {
                    updateEvent()
                }

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


        binding.etTitle.setText(title)
        binding.etDes.setText(message)
        binding.textDay.text = day
        binding.textTime.text = time

    }

    private fun addPhoto() {
        binding.cvAddPhoto.setOnClickListener {
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

                setupAdapter(listOf(imageUri))

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                toastMessage("eror")
            } else {
                toastMessage("okey")
            }
        }

    private fun setupAdapter(uriList: List<Uri>) {
        this.uriList.addAll(uriList)
        adapter = ImageViewAdapter().apply {
            submitList(this@DetailFragment.uriList)
        }
        binding.rvIvPicker.adapter = adapter
    }

    private fun savePhotoFireBase(list: List<Uri>) {
        viewModel.uploadPhoto(list)
    }


    private fun getFromLocation(): Boolean {
        from = arguments?.getString("from")
        return when (from) {
            "fab" -> true
            "recyclerview" -> {
                showData()
                false
            }

            else -> false
        }
    }

    private fun navigateListingFragment() {
        findNavController().navigate(R.id.action_detailFragment_to_listingFragment)
    }



}

