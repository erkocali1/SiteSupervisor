package com.muzo.sitesupervisor.feature.fragment.settingsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.addStatusBarColorUpdate
import com.muzo.sitesupervisor.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        binding.toolbar.title = "AYARLAR"

        binding.cvLocation.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_locationFragment)
        }

        return binding.root
    }


}