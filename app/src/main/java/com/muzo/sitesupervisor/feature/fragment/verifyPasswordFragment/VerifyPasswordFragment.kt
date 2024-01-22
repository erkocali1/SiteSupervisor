package com.muzo.sitesupervisor.feature.fragment.verifyPasswordFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.data.model.UserInfo
import com.muzo.sitesupervisor.databinding.FragmentVerifyPasswordBinding
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.aestheticdialogs.OnDialogClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

@AndroidEntryPoint
class VerifyPasswordFragment : Fragment() {
    private lateinit var binding: FragmentVerifyPasswordBinding
    private lateinit var stateJob: Job
    private val viewModel: VerifyPasswordFragmentViewModel by viewModels()
    private lateinit var constructionArea: String
    private lateinit var sitePassword: String
    private lateinit var siteSupervisor: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVerifyPasswordBinding.inflate(layoutInflater, container, false)
        infAlert()
        getSiteInfo()
        viewModel.getPassword(siteSupervisor, constructionArea)
        observeData()
        setEditTextListeners(
            arrayOf(
                binding.editText1,
                binding.editText2,
                binding.editText3,
                binding.editText4,
                binding.editText5))
        clearEditTexts()
        return binding.root
    }


    private fun infAlert() {
        AestheticDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.INFO)
            .setTitle("Şantiye Şifresini Girin").setCancelable(false)
            .setMessage("Bilgileri görüntülemek için şantiye şefi tarafından belirlenen şifreyi girin")
            .setDarkMode(false).setGravity(Gravity.CENTER).setAnimation(DialogAnimation.DEFAULT)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                    //actions...
                }
            }).show()
    }

    private fun observeData() {
        stateJob = lifecycleScope.launch {
            viewModel.getPasswordState.collect { getPasswordState ->
                when {
                    getPasswordState.loading -> {
                        binding.progressBar.show()
                    }

                    getPasswordState.password != null -> {
                        binding.progressBar.hide()
                        val password = getPasswordState.password
                        sitePassword = password
                    }
                }
            }
        }
    }

    private fun setEditTextListeners(editTexts: Array<EditText>) {
        val passwordStringBuilder = StringBuilder()

        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (count == 1) {
                        // Bir karakter eklendi
                        passwordStringBuilder.append(s)
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        } else {
                            checkPassword(passwordStringBuilder.toString())
                        }
                    } else if (count == 0 && before == 1) {
                        // Bir karakter silindi
                        passwordStringBuilder.deleteCharAt(passwordStringBuilder.length - 1)
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
    }
    private fun checkPassword(password: String) {
        if (sitePassword == password) {
            navigateFragment()
            toastMessage("Şifre Onaylandı", requireContext())
        } else {
            toastMessage("Şifre Eşleşmedi Tekrar Deneyiniz", requireContext())
        }
    }


    private fun clearEditTexts() {
        binding.btnClearText.setOnClickListener {
            val editTexts = arrayOf(
                binding.editText1,
                binding.editText2,
                binding.editText3,
                binding.editText4,
                binding.editText5
            )

            for (editText in editTexts) {
                editText.text = null
            }
            editTexts[0].requestFocus()

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

    private fun navigateFragment() {
        findNavController().navigate(R.id.action_verifyPasswordFragment_to_listingFragment)
    }
}


