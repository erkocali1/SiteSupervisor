package com.muzo.sitesupervisor.feature.fragment.registerfragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.data.model.UserInfo
import com.muzo.sitesupervisor.databinding.FragmentRegisterBinding
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.aestheticdialogs.OnDialogClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterFragmentViewModel by viewModels()
    private lateinit var name: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        observeData()
        clickEvent()
        infAlert()
        return binding.root
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.signUpFlow.collect { firebaseUser ->
                when (firebaseUser) {
                    is Resource.Error -> toastMessage(firebaseUser.exception?.message!!)
                    Resource.Loading -> binding.progressBar.show()
                    is Resource.Success -> {
                        binding.progressBar.hide()
                        firebaseUser.data?.let { user ->
                            getInfo(user)
                        }
                        toastMessage("Kayıt İşlemi Başarılı bir şekilde Tamamlandı.")
                        navigateFragment()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun clickEvent() {
        binding.checkboxText.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_vebViewFragment)
        }



        binding.btnRegister.setOnClickListener {
            val nameText = binding.etUserName.text.toString()
            val email = binding.etMail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val checkBoxChecked = binding.checkbox.isChecked

            if (nameText.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (confirmPassword == password) {
                    if (checkBoxChecked) {
                        name = nameText
                        viewModel.signUp(nameText, email, password)
                    } else {
                        toastMessage("Lütfen Gizlilik Sözleşmesini Onaylayın")
                        findNavController().navigate(R.id.action_registerFragment_to_vebViewFragment)

                    }
                } else {
                    toastMessage("Şifre Eşleşmiyor")
                }
            } else {
                toastMessage("Boş Alanları Doldurunuz")
            }
        }
    }


    private fun getInfo(fireBaseUser: FirebaseUser) {
        val mail = fireBaseUser.email
        val uid = fireBaseUser.uid
        val name = fireBaseUser.displayName

        val userInfo = UserInfo(
            email = mail,
            name = name
        )
        viewModel.addUserInfo(uid, userInfo)
    }

    private fun navigateFragment() {
        findNavController().navigate(R.id.action_registerFragment_to_selectionFragment)
    }

    private fun toastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun infAlert() {
        AestheticDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.INFO)
            .setTitle("Kayıt İşlemi").setCancelable(false)
            .setMessage("Lütfen kayıt olmak için gerekli bilgileri doldurun ayrıca devam etmek için gizlilik sözleşmesini okuyun ve onaylayın.")
            .setDarkMode(false).setGravity(Gravity.CENTER).setAnimation(DialogAnimation.DEFAULT)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                    //actions...
                }
            }).show()
    }

}