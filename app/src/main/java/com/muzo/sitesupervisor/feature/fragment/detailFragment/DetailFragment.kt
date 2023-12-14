package com.muzo.sitesupervisor.feature.fragment.detailFragment

import android.app.Activity
import android.net.Uri
import android.os.Bundle
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
import com.muzo.sitesupervisor.feature.adapters.listingimageadapter.ListingImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val viewModel: DetailFragmentViewModel by viewModels()
    private lateinit var binding: FragmentDetailBinding
    private var uriList: MutableList<Uri> = mutableListOf()
    private var from: String? = null
    private var postId: Long? = null
    private lateinit var constructionArea: String
    private var saveDataJob: Job? = null
    private var saveDataWithPhotoJob: Job? = null
    private var updateData: Job? = null
    private var updateDataWithPhoto: Job? = null
    private var updateItJob: Job? = null
    private lateinit var adapterImage: ListingImageAdapter
    private lateinit var localDataRoom: DataModel
    private var photoList: List<String>? = null
    private var stringList: List<String>? = null
    private var isEnterInitialized = false
    private var changedFlag: Boolean = false
    private var getRoomDataJob: Job? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)


        lifecycleScope.launch {
            viewModel.readDataStore("construction_key").collect { area ->
                constructionArea = area!!
            }
        }

        getFromLocation()
        showDayAndTime()
        clickListener()
        addPhoto()
        turnBackFragment()
        getAllPhoto()

        return binding.root
    }

    private fun showDayAndTime() {
        if (from == "fab") {
            binding.cvModificationConstant.hide()
            binding.modificationTime.hide()
            binding.modificationDay.hide()
            val (currentDate, currentTime) = viewModel.getCurrentDateAndTime()
            binding.textDay.text = currentDate
            binding.textTime.text = currentTime
        }

    }

    private fun takeData(): DataModel {

        val receivedData = arguments?.getParcelable<DataModel>("dataList")
        val title = binding.etTitle.text.toString()
        val message = binding.etDes.text.toString()
        val photoUrl = uriList
        val stringUriList = photoUrl?.map { it.toString() }
        val day = binding.textDay.text.toString()
        val time = binding.textTime.text.toString()
        val currentUser = viewModel.currentUser
        postId = receivedData?.id
        return DataModel(
            postId, message, title, stringUriList, day, time, currentUser, constructionArea
        )

    }

    private fun saveNewDataEvent() {

        val data = takeData()

        saveDataJob = lifecycleScope.launch {

            val newList = uriList.map { it.toString() }

            postId = viewModel.saveRoom(data.copy(photoUrl = newList))
            data.id = postId

            addImageToFirebaseStorage(uriList, data.id.toString())

            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()
                    }

                    uiState.resultUriList != null -> {
                        binding.progressBar.hide()
                        val gettingUriList = uiState.resultUriList
                        val stringUriList = gettingUriList?.map { it.toString() } ?: emptyList()
                        //link add to firebase database
                        viewModel.updatePhoto(postId!!, stringUriList)

                        observeImageUpload(data, stringUriList)
                        saveDataJob?.cancel()
                    }
                }
            }
        }
    }

    private fun observeImageUpload(data: DataModel, photoUrl: List<String>?) {
        viewModel.addData(data.copy(photoUrl = photoUrl))
    }

    private fun updateEvent() {

        val postIdNew = arguments?.getLong("id")

        val updatedPostId = if (postId == null) postIdNew else postId

        val (modificationDate, modificationTime) = viewModel.getCurrentDateAndTime()

        val dataModel = takeData().copy(
            id = updatedPostId,
            modificationDate = modificationDate,
            modificationTime = modificationTime
        )

        addImageToFirebaseStorage(uriList, dataModel.id.toString())

        updateItJob = lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()
                    }

                    uiState.resultUriList != null -> {
                        binding.progressBar.hide()
                        val gettingUriList = uiState.resultUriList
                        val stringUriList = gettingUriList?.map { it.toString() } ?: emptyList()
                        val allList = stringList?.plus(stringUriList)
                        viewModel.updateAllPost(updatedPostId!!, dataModel.copy(photoUrl = allList))
                        observeImageUpload(dataModel, allList)
                        updateItJob?.cancel()
                    }
                }
            }
        }
    }

    private fun clickListener() {
        binding.okBtn.setOnClickListener {
            if (isAllFieldsFilled()) {
                if (getFromLocation()) {
                    saveNewDataEvent()
                    var clickListener: Job? = null
                    clickListener = lifecycleScope.launch {
                        viewModel.uiState.collect { uiState ->
                            when {
                                (uiState.loading) -> {
                                    binding.progressBar.show()
                                }

                                uiState.isSuccessfulAddData -> {
                                    binding.progressBar.hide()
                                    navigateListingFragment()
                                    clickListener?.cancel()
                                }
                            }
                        }
                    }
                } else {
                    if (correctText(photoList)) {
                        updateEvent()
                        updateData = lifecycleScope.launch {
                            viewModel.uiState.collect { uiState ->
                                when {
                                    uiState.isSuccessfulAddData -> {
                                        binding.progressBar.hide()
                                        navigateListingFragment()
                                        updateData?.cancel()
                                    }

                                    uiState.loading -> {
                                        binding.progressBar.show()
                                    }
                                }
                            }
                        }
                    } else {
                        navigateListingFragment()
                    }

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
        val receivedData = arguments?.getLong("id")

        getRoomDataJob = lifecycleScope.launch {
            receivedData?.let {
                viewModel.getDataFromRoom(receivedData)
            }
            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()
                    }

                    uiState.localData != null -> {
                        binding.progressBar.hide()

                        localDataRoom = uiState.localData

                        stringList = localDataRoom.photoUrl


                        adapterImage = ListingImageAdapter(stringList) {
                            navigateToBigPhotoFragment(it)
                        }
                        binding.rvIvPicker.adapter = adapterImage

                        binding.etTitle.setText(localDataRoom.title)
                        binding.etDes.setText(localDataRoom.message)
                        binding.textDay.text = localDataRoom.day
                        binding.textTime.text = localDataRoom.time



                        if (localDataRoom.modificationDate?.isNotEmpty() == true) {
                            binding.modificationTime.text = localDataRoom.modificationTime
                            binding.modificationDay.text = localDataRoom.modificationDate
                        } else {
                            binding.cvModificationConstant.hide()
                            binding.modificationTime.hide()
                            binding.modificationDay.hide()
                        }
                        getRoomDataJob?.cancel()

                    }
                }
            }
        }
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
                uriList.add(imageUri)

                //           this.stringList.addAll(stringList)
                if (stringList != null) {
                    setupPhotoAdapter(stringList!! + uriList.map { it.toString() })
                } else {
                    setupPhotoAdapter(uriList.map { it.toString() })
                }
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                toastMessage("eror")
            } else {
                toastMessage("okey")
            }
        }


    private fun setupPhotoAdapter(stringList: List<String>) {
        adapterImage = ListingImageAdapter(stringList) { navigateToBigPhotoFragment(it) }
        binding.rvIvPicker.adapter = adapterImage
    }


    private fun addImageToFirebaseStorage(list: List<Uri>?, postId: String) {
        viewModel.addImageToFirebaseStorage(list, postId)
    }


    private fun getFromLocation(): Boolean {
        from = arguments?.getString("from")
        return when (from) {
            "fab" -> true
            "recyclerview" -> {
                if (!isEnterInitialized) {
                    showData()
                    isEnterInitialized = true
                }
                false
            }

            else -> false
        }
    }

    private fun navigateListingFragment() {
        findNavController().navigate(R.id.action_detailFragment_to_listingFragment)

    }

    private fun turnBackFragment() {
        binding.back.setOnClickListener {
            if (from == "recyclerview") {
                if (correctText(photoList)) {
                    updateEvent()
                    updateDataWithPhoto = lifecycleScope.launch {
                        viewModel.uiState.collect { uiState ->
                            when {
                                uiState.isSuccessfulAddData -> {
                                    binding.progressBar.hide()
                                    updateDataWithPhoto?.cancel()
                                    navigateListingFragment()
                                }

                                uiState.loading -> {
                                    binding.progressBar.show()
                                }
                            }
                        }
                    }
                } else {
                    navigateListingFragment()
                }
            } else {
                navigateListingFragment()
            }
        }
    }

    private fun navigateToBigPhotoFragment(uri: String) {
        if (from == "recyclerview") {
            if (correctText(photoList)) {
                updateEvent()
                updateDataWithPhoto = lifecycleScope.launch {
                    viewModel.uiState.collect { uiState ->
                        when {
                            uiState.isSuccessfulAddData -> {
                                binding.progressBar.hide()
                                val receivedData = arguments?.getLong("id")
                                val bundle = Bundle().apply {
                                    putString("bigPhoto", uri)
                                    putLong("id", receivedData!!)
                                }
                                findNavController().navigate(
                                    R.id.action_detailFragment_to_photoFragment, bundle
                                )
                                updateDataWithPhoto?.cancel()
                            }

                            uiState.loading -> {
                                binding.progressBar.show()
                            }
                        }
                    }
                }
            } else {
                val receivedData = arguments?.getLong("id")
                val bundle = Bundle().apply {
                    putString("bigPhoto", uri)
                    putLong("id", receivedData!!)
                }
                findNavController().navigate(
                    R.id.action_detailFragment_to_photoFragment, bundle
                )
            }
        } else {
            saveNewDataEvent()
            saveDataWithPhotoJob = lifecycleScope.launch {
                viewModel.uiState.collect { uiState ->
                    when {
                        (uiState.loading) -> {
                            binding.progressBar.show()
                        }

                        uiState.isSuccessfulAddData -> {
                            binding.progressBar.hide()
                            val bundle = Bundle().apply {
                                putString("bigPhoto", uri)
                                putLong("id", postId!!)
                            }
                            findNavController().navigate(
                                R.id.action_detailFragment_to_photoFragment, bundle
                            )
                            saveDataWithPhotoJob?.cancel()
                        }
                    }
                }
            }
        }
    }

    private fun correctText(photoList: List<String>?): Boolean {

        val title = binding.etTitle.text.toString()
        val desc = binding.etDes.text.toString()
        val newPhotoList = photoList
        val localList = localDataRoom.photoUrl?.plus(uriList.map { it.toString() })

        if (title != localDataRoom.title || desc != localDataRoom.message || newPhotoList != localList) {
            changedFlag = true
        }
        return changedFlag
    }

    private fun getAllPhoto() {
        val currentUser = viewModel.currentUser
        val receivedData = arguments?.getLong("id")
        postId = receivedData
        viewModel.getAllPhoto(currentUser, constructionArea, postId.toString())
        var ff: Job? = null
        ff = lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()
                    }

                    uiState.photoList != null -> {
                        binding.progressBar.hide()
                        photoList = uiState.photoList
                        ff?.cancel()
                    }
                }
            }
        }
    }
}

