package com.muzo.sitesupervisor.feature.fragment.loginfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        observeData()
        clickEvent()

        return binding.root
    }

    private fun observeData() {

        lifecycleScope.launch {
            viewModel.loginFlow.collect {
                when (it) {
                    is Resource.Error -> toastMessage(it.exception?.message!!)
                    Resource.Loading -> {}
                    is Resource.Success -> navigateFragment()
                    else -> {}

                }
            }
        }
    }

    private fun clickEvent() {

        binding.btnLogin.setOnClickListener {

            val email = binding.etMail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                toastMessage("Name and password cannot be empty")
            }
        }
    }

    private fun navigateFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_selectionFragment)
    }

    private fun toastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}