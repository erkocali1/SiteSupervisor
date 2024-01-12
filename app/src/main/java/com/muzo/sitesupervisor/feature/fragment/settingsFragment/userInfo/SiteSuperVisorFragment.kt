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
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.data.model.UserInfo
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
        viewModel.getSiteSuperVisorInfo(siteSupervisor)
        changePhoto()
        observeData("getData")
        editData()
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
                                            resultList.toString(),
                                            siteSupervisor,
                                            ItemType.PHOTO_URL.key
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

                    "getData" -> {
                        launch {
                            viewModel.getInfoState.collect { getInfoState ->
                                when {
                                    getInfoState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    getInfoState.userInfoList != null -> {
                                        binding.progressBar.hide()
                                        showKnownData(getInfoState.userInfoList)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun editData() {
        binding.icEditSiteSupervisor.setOnClickListener {
            binding.icEditSiteSupervisor.hide()
            val textName = binding.name.text.toString()
            binding.etSiteSupervisor.setText(textName)
            binding.name.hide()
            binding.etSiteSupervisor.show()
            binding.icDoneSiteSupervisor.show()
        }
        binding.icDoneSiteSupervisor.setOnClickListener {
            val editTextName = binding.etSiteSupervisor.text.toString()
            viewModel.addUrlToFireBase(editTextName, siteSupervisor, ItemType.NAME.key)
            binding.icDoneSiteSupervisor.hide()
            binding.icEditSiteSupervisor.show()
            binding.name.show()
            binding.etSiteSupervisor.hide()
            binding.name.text = editTextName
        }

        binding.icEditPhone.setOnClickListener {
            binding.icEditPhone.hide()
            val phoneText = binding.phoneNumber.text.toString()
            binding.etPhone.setText(phoneText)
            binding.phoneNumber.hide()
            binding.etPhone.show()
            binding.icDonePhoneNumber.show()
        }
        binding.icDonePhoneNumber.setOnClickListener {
            val editPhoneNumber = binding.etPhone.text.toString()
            viewModel.addUrlToFireBase(editPhoneNumber, siteSupervisor, ItemType.PHONE_NUMBER.key)
            binding.icDonePhoneNumber.hide()
            binding.icEditPhone.show()
            binding.etPhone.hide()
            binding.phoneNumber.show()
            binding.phoneNumber.text = editPhoneNumber
        }

        binding.icMailEdit.setOnClickListener {
            binding.icEditPhone.hide()
            val mailText = binding.mail.text.toString()
            binding.etMail.setText(mailText)
            binding.icEditPhone.hide()
            binding.icMailDone.show()
            binding.etMail.show()
        }
        binding.icMailDone.setOnClickListener {
            val editMail = binding.etPhone.text.toString()
            viewModel.addUrlToFireBase(editMail, siteSupervisor, ItemType.MAIL.key)
            binding.icMailDone.hide()
            binding.icEditPhone.show()
            binding.mail.show()
            binding.etMail.hide()
            binding.mail.text = editMail

        }

    }

    private fun showKnownData(userInfo: UserInfo) {
        binding.name.text = userInfo.name
        binding.mail.text = userInfo.email
        binding.phoneNumber.text = userInfo.phoneNumber
        val uri = userInfo.photoUrl
        if (uri?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(uri)
                .into(binding.personPhoto)
        } else {
            binding.personPhoto.setImageResource(R.drawable.ic_person)
        }
    }

}

enum class ItemType(val key: String) {
    NAME("name"),
    PHOTO_URL("photoUrl"),
    PHONE_NUMBER("phoneNumber"),
    MAIL("email")
}