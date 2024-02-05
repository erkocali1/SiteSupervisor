package com.muzo.sitesupervisor.feature.fragment.paswordFragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.databinding.FragmentPasswordBinding
import com.muzo.sitesupervisor.feature.fragment.settingsFragment.userInfo.ItemType
import com.muzo.sitesupervisor.feature.fragment.settingsFragment.userInfo.SiteSuperVisorViewModel
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.aestheticdialogs.OnDialogClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordFragment : Fragment() {
    private lateinit var binding: FragmentPasswordBinding
    private val viewModel: PasswordFragmentViewModel by viewModels()
    private lateinit var updateItJob: Job
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPasswordBinding.inflate(layoutInflater, container, false)

        getSiteInfo()
        infAlert()
        setEditTextListeners(
            arrayOf(
                binding.editText1,
                binding.editText2,
                binding.editText3,
                binding.editText4,
                binding.editText5
            )
        )
        backPressEvent()
        setPassword()
        return binding.root
    }

    private fun setEditTextListeners(editTexts: Array<EditText>) {
        val passwordStringBuilder = StringBuilder()
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    if (s?.length == 1) {
                        passwordStringBuilder.append(s)
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        } else {
                            val password = passwordStringBuilder.toString()
                         //   val user = viewModel.currentUser?.uid.toString()
                            viewModel.changePassword(password,siteSupervisor,constructionArea)
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun infAlert() {
        AestheticDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.INFO)
            .setTitle("Şantiye Şifresini Belirleyin").setCancelable(false)
            .setMessage("Şantiye şifrenizi şantiye alanını görüntülemesini İstediğiniz kişilerle paylaşın")
            .setDarkMode(false).setGravity(Gravity.CENTER).setAnimation(DialogAnimation.DEFAULT)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                    //actions...
                }
            }).show()
    }

    private fun observeData() {
        updateItJob = lifecycleScope.launch {
            viewModel.loadPasswordState.collect { photoLoadState ->
                when {
                    photoLoadState.loading -> {
                        binding.progressBar.show()
                    }

                    photoLoadState.result -> {
                        binding.progressBar.hide()
                        toastMessage("Şifre Başarılı Bir Şekilde Kaydedildi", requireContext())
                        navigateFragment()
                    }
                }
            }
        }
    }

    private fun deleteData() {
        updateItJob = lifecycleScope.launch {
            viewModel.deleteAreaState.collect { deleteAreaState ->
                when {
                    deleteAreaState.loading -> {
                        binding.progressBar.show()
                    }

                    deleteAreaState.result -> {
                        binding.progressBar.hide()
                        toastMessage("Şantiye şifresi oluştrulmadı kayıt siliniyor",requireContext())
                        findNavController().popBackStack(R.id.selectionFragment, false)
                    }
                }
            }
        }
    }

    private fun navigateFragment() {
        findNavController().navigate(R.id.action_passwordFragment_to_listingFragment)
    }

    private fun setPassword() {
        binding.btnSetPassword.setOnClickListener {
            val enteredPassword = getEnteredPassword()
            if (enteredPassword.length == 5) {
                observeData()
            } else {
                toastMessage("Lütfen 5 haneli bir şifre giriniz", requireContext())
            }
        }
    }

    private fun getEnteredPassword(): String {
        val passwordStringBuilder = StringBuilder()
        passwordStringBuilder.append(binding.editText1.text)
        passwordStringBuilder.append(binding.editText2.text)
        passwordStringBuilder.append(binding.editText3.text)
        passwordStringBuilder.append(binding.editText4.text)
        passwordStringBuilder.append(binding.editText5.text)
        return passwordStringBuilder.toString()
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
    private fun backPressEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.deleteArea(siteSupervisor,constructionArea)
            deleteData()
        }
    }
}
