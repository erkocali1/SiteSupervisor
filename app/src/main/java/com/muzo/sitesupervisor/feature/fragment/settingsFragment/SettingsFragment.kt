package com.muzo.sitesupervisor.feature.fragment.settingsFragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.constans.Constants.Companion.REQUEST_CODE_LOCATION_PERMISSION
import com.muzo.sitesupervisor.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var currentUser: String
    private val viewModel: SettingsFragmentViewModel by viewModels()
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        binding.toolbar.title = "AYARLAR"
        clickEvents()
        currentUser = viewModel.currentUser
        getSiteInfo()
        validationUser()
        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // İstek kodu bizim belirlediğimiz koda eşitse
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            // İzin verilmişse konum bilgisini kullanın
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Konum izni verildi", Toast.LENGTH_SHORT).show()
                // Konum bilgisini kullanma kodu buraya gelecek
            } else {
                // İzin verilmemişse uygun bir mesaj gösterin
                Toast.makeText(requireContext(), "Konum izni verilmedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun locationEvent() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            toastMessage("Konum İzini Alındı.", requireContext())
            findNavController().navigate(R.id.action_settingsFragment_to_locationFragment)
            // Konum bilgisini kullanma kodu buraya gelecek
        } else {
            // Konum izni verilmemişse izin isteyin
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }

    private fun clickEvents() {
        binding.cvLocation.setOnClickListener {
            locationEvent()
        }
        binding.cvPerson.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_siteSuperVisorFragment)
        }

        binding.cvPasswordSettings.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_settingsPasswordFragment)

        }
        binding.cvTeam.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_teamFragment)
        }
        binding.cvUser.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_userFragment)
        }
        binding.cvPdf.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_pdfFragment)
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
    private fun validationUser(){
        if (siteSupervisor!=currentUser){
            binding.cvTeam.hide()
            binding.cvPdf.hide()
            binding.cvPasswordSettings.hide()
            binding.cvUser.show()
        }
    }

}





