package com.muzo.sitesupervisor.feature.fragment.registerfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        observeData()
        clickEvent()
        return binding.root
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.signUpFlow.collect {firebaseUser->
                when (firebaseUser) {
                    is Resource.Error -> toastMessage(firebaseUser.exception?.message!!)
                    Resource.Loading -> binding.etUserName.visibility = View.GONE
                    is Resource.Success -> {
                        navigateFragment()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun clickEvent() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etUserName.text.toString()
            val email = binding.etMail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (confirmPassword == password) {
                    viewModel.signUp(name, email, password)

                } else {
                    toastMessage("Password does not Match")
                }

            } else {
                toastMessage("Empty Elements are not Allowed")
            }

        }
    }

    private fun navigateFragment() {
        findNavController().navigate(R.id.action_registerFragment_to_selectionFragment)
    }

    private fun toastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}