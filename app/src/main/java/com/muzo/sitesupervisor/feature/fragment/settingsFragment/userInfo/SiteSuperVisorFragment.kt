package com.muzo.sitesupervisor.feature.fragment.settingsFragment.userInfo

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.databinding.FragmentSiteSuperVisorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SiteSuperVisorFragment : Fragment() {
    private lateinit var binding: FragmentSiteSuperVisorBinding
    private val viewModel: SiteSuperVisorViewModel by viewModels()
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private lateinit var updateItJob: Job


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSiteSuperVisorBinding.inflate(layoutInflater, container, false)

        getSiteInfo()
        setData()
        changePhoto()
        return binding.root
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val imageUri = data?.data ?: return@registerForActivityResult
                Glide.with(requireContext())
                    .load(imageUri)
                    .into(binding.personPhoto)
                viewModel.addUserPhoto(imageUri, siteSupervisor)
                observeData("file")
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                toastMessage("eror", requireContext())
            } else {
                toastMessage("okey", requireContext())
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

    private fun setData() {
        binding.construcitonName.text = constructionArea
        binding.name.text = viewModel.currentUser?.displayName
        binding.mail.text = viewModel.currentUser?.email
    }

    private fun changePhoto() {

        binding.loadPhoto.setOnClickListener {
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

    private fun observeData(observedState: String) {
        updateItJob = lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (observedState) {
                    "file" -> {
                        launch {
                            viewModel.photoLoadState.collect { photoLoadState ->
                                when {
                                    photoLoadState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    photoLoadState.resultUriList != null -> {
                                        val resultList = photoLoadState.resultUriList
                                        binding.progressBar.hide()
                                        viewModel.addUrlToFireBase(
                                            resultList,
                                            siteSupervisor,
                                            constructionArea
                                        )
                                        observeData("save")
                                    }
                                }
                            }
                        }
                    }

                    "save" -> {
                        launch {
                            viewModel.loadUrlState.collect { loadUrlState ->
                                when {
                                    loadUrlState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    loadUrlState.result -> {
                                        toastMessage(
                                            "Fotoğraf Başarılı Bir Şekilde Kaydedildi",
                                            requireContext()
                                        )
                                        binding.progressBar.hide()
                                        updateItJob.cancel()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}