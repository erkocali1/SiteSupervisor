package com.muzo.sitesupervisor.feature.fragment.settingsFragment.settingsPassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.databinding.FragmentSettingsPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SettingsPasswordFragment : Fragment() {
    private lateinit var binding: FragmentSettingsPasswordBinding
    private val viewModel: SettingsPasswordFragmentViewModel by viewModels()
    private lateinit var updateJob: Job
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private lateinit var sitePassword: String
    private lateinit var password: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingsPasswordBinding.inflate(layoutInflater, container, false)
        getSiteInfo()
        viewModel.getPassword(siteSupervisor, constructionArea)
        observeData("get")
        setEditTextListeners(
            arrayOf(
                binding.editText1,
                binding.editText2,
                binding.editText3,
                binding.editText4,
                binding.editText5
            )
        )
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
                             password = passwordStringBuilder.toString()
                            //   val user = viewModel.currentUser?.uid.toString()
                            viewModel.changePassword(password, siteSupervisor, constructionArea)
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun observeData(observedState: String) {
        updateJob = lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (observedState) {
                    "change" -> {
                        launch {
                            viewModel.changePasswordState.collect { changePasswordState ->
                                when {
                                    changePasswordState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    changePasswordState.result -> {
                                        binding.progressBar.hide()
                                        toastMessage(
                                            "Şifre Başarılı Bir Şekilde Değiştirildi",
                                            requireContext()
                                        )
                                        binding.currentPassword.text=password
                                        updateJob.cancel()
                                    }
                                }
                            }
                        }
                    }

                    "get" -> {
                        launch {
                            viewModel.getPasswordState.collect { getPasswordState ->
                                when {
                                    getPasswordState.loading -> {
                                        binding.progressBar.show()
                                        binding.constraint.hide()
                                    }

                                    getPasswordState.password != null -> {
                                        sitePassword = getPasswordState.password
                                        binding.currentPassword.text = sitePassword
                                        binding.progressBar.hide()
                                        binding.constraint.show()

                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private fun setPassword() {
        binding.btnSetPassword.setOnClickListener {
            val enteredPassword = getEnteredPassword()
            if (enteredPassword.length == 5) {
                observeData("change")
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
}