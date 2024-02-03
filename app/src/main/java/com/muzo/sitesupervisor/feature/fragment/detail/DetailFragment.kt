package com.muzo.sitesupervisor.feature.fragment.detail

import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val viewModel: DetailFragmentViewModel by viewModels()
    private lateinit var binding: FragmentDetailBinding
    private var uriList: MutableList<Uri> = mutableListOf()
    private var from: String? = null
    private var postId: Long? = null
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private lateinit var currentUser: String
    private var saveDataJob: Job? = null
    private var saveDataWithPhotoJob: Job? = null
    private var updateData: Job? = null
    private var updateDataWithPhoto: Job? = null
    private var updateItJob: Job? = null
    private var checkout: Job? = null
    private lateinit var adapterImage: ListingImageAdapter
    private var stringList: List<String>? = null
    private var isEnterInitialized = false
    private var changedFlag: Boolean = false
    private lateinit var getData: DataModel
    private var isDeletePhoto = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)


        lifecycleScope.launch {
            viewModel.readDataStore("construction_key").collect { area ->
                constructionArea = area!!
                viewModel.readDataStore("user_key").collect { Supervisor ->
                    siteSupervisor = Supervisor!!
                }
            }
        }

        currentUser = viewModel.currentUser
        backStackEvent()
        isDeleteChange()
        getFromLocation()
        showDayAndTime()
        clickListener()
        addPhoto()
        turnBackFragment()
        hideButton()
        deletePostEvent()
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
        val currentUser = siteSupervisor
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
                    if (correctText() || isDeletePhoto) {
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

    private fun getData() {
        val receivedData = arguments?.getLong("id")
        checkout = lifecycleScope.launch {
            viewModel.getData(siteSupervisor, constructionArea, receivedData.toString())
            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()
                        hideViews(true)
                    }

                    uiState.getDataFireBase != null -> {
                        binding.progressBar.hide()
                        getData = uiState.getDataFireBase
                        bind(getData)
                        hideViews(false)
                        checkout?.cancel()
                    }
                }
            }
        }
    }

    private fun bind(item: DataModel) {

        stringList = item.photoUrl
        adapterImage = ListingImageAdapter(stringList) {
            navigateToBigPhotoFragment(it)
        }
        binding.rvIvPicker.adapter = adapterImage

        binding.etTitle.setText(item.title)
        binding.etDes.setText(item.message)
        binding.textDay.text = item.day
        binding.textTime.text = item.time

        if (item.modificationDate?.isNotEmpty() == true) {
            binding.modificationTime.text = item.modificationTime
            binding.modificationDay.text = item.modificationDate
        } else {
            binding.cvModificationConstant.hide()
            binding.modificationTime.hide()
            binding.modificationDay.hide()
        }
    }

    private fun hideViews(showItem: Boolean) {
        if (showItem) {
            binding.gruopItem.hide()

        } else {
            binding.gruopItem.show()

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
                    getData()
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
            if (currentUser == siteSupervisor) {
                if (from == "recyclerview" || isDeletePhoto) {
                    if (correctText()) {
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
            } else {
                navigateListingFragment()
            }
        }
    }

    private fun navigateToBigPhotoFragment(uri: String) {
        if (currentUser == siteSupervisor) {
            if (from == "recyclerview") {
                if (correctText()) {
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
                    postId = receivedData!!
                    val bundle = Bundle().apply {
                        putString("bigPhoto", uri)
                        putLong("id", postId!!)
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
                                val bundle = Bundle().apply {
                                    putString("bigPhoto", uri)
                                    putLong("id", postId!!)
                                }
                                binding.progressBar.hide()
                                findNavController().navigate(
                                    R.id.action_detailFragment_to_photoFragment, bundle
                                )
                                saveDataWithPhotoJob?.cancel()
                            }
                        }
                    }
                }
            }
        } else {
            val bundle = Bundle().apply {
                val receivedData = arguments?.getLong("id")
                postId = receivedData!!
                putString("bigPhoto", uri)
                putLong("id", postId!!)

            }
            findNavController().navigate(R.id.action_detailFragment_to_photoFragment, bundle)
        }

    }

    private fun correctText(): Boolean {

        val title = binding.etTitle.text.toString()
        val desc = binding.etDes.text.toString()
        val changeList = stringList?.plus(uriList.map { it.toString() })

        if (title != getData.title || desc != getData.message || stringList != changeList) {
            changedFlag = true
        }
        return changedFlag
    }

    private fun isDeleteChange() {
        isDeletePhoto = arguments?.getBoolean("isDelete") ?: false
    }

    private fun hideButton() {
        if (siteSupervisor != currentUser) {
            binding.okBtn.hide()
            binding.okDelete.hide()
            binding.cvAddPhoto.hide()
            binding.etDes.isEnabled = false
            binding.etTitle.isEnabled = false
        }
    }

    private fun backStackEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // D fragmentından B fragmentına kadar olan tüm fragmentları geri al
            findNavController().popBackStack(R.id.listingFragment, false)
        }
    }

    private fun deletePostEvent() {
        binding.okDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }
    private fun observeDelete() {
        val receivedData = arguments?.getLong("id")
        postId = receivedData!!
        viewModel.deletePost(siteSupervisor, constructionArea, postId.toString())
        lifecycleScope.launch {
            viewModel.deleteState.collect { deleteState ->
                when {
                    deleteState.loading -> {
                        binding.progressBar.show()
                    }
                    deleteState.isDelete -> {
                        binding.progressBar.hide()
                        toastMessage("Kayıt Başarılı Bir Şekilde Silindi")
                        navigateListingFragment()
                    }
                }
            }
        }
    }
    private fun showDeleteConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Kayıt Sil")
        alertDialogBuilder.setMessage("Bu kayıt silindiğinde bu kayıda ait tüm veriler silinicektir.Onaylıyor musunuz?")
        alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
            observeDelete()
        }
        alertDialogBuilder.setNegativeButton("Hayır") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}