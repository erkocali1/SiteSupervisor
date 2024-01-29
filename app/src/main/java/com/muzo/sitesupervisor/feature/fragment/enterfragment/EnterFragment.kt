package com.muzo.sitesupervisor.feature.fragment.enterfragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.Resource
import com.muzo.sitesupervisor.core.constans.Constants.Companion.KEY_IS_ENTERED
import com.muzo.sitesupervisor.core.constans.Constants.Companion.PREF_NAME
import com.muzo.sitesupervisor.databinding.FragmentEnterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EnterFragment : Fragment() {
    private lateinit var binding: FragmentEnterBinding
    private val viewModel: EnterFragmentViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnterBinding.inflate(layoutInflater, container, false)

        if (isEntered()) {
            findNavController().navigate(R.id.action_enterFragment_to_listingFragment)
        } else {
            checkLogin()
        }
        setButtonClickListeners()



        return binding.root
    }

    private fun navigateFragment(destinationId: Int) {
        findNavController().navigate(destinationId)
    }

    private fun setButtonClickListeners() {
        with(binding) {
            btnLogin.setOnClickListener { navigateFragment(R.id.action_enterFragment_to_loginFragment) }
            btnRegister.setOnClickListener { navigateFragment(R.id.action_enterFragment_to_registerFragment) }
        }

    }

    private fun checkLogin() {
        lifecycleScope.launch {
            viewModel.userInfo.collect {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Success -> navigateToSelectionFragment()
                    Resource.Loading -> {}
                    else -> {}
                }
            }
        }
    }

    private fun navigateToSelectionFragment() {
        findNavController().navigate(R.id.action_enterFragment_to_selectionFragment)
    }

    private fun isEntered(): Boolean {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_IS_ENTERED, false)
    }
}