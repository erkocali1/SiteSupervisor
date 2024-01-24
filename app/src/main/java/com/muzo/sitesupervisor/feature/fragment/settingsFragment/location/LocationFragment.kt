package com.muzo.sitesupervisor.feature.fragment.settingsFragment.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.databinding.FragmentLocationBinding
import com.muzo.sitesupervisor.feature.fragment.taskFragment.ObservedState
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.aestheticdialogs.OnDialogClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationFragment : Fragment() {
    private lateinit var binding: FragmentLocationBinding
    private val viewModel: LocationFragmentViewModel by viewModels()
    private var map: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private lateinit var updateItJob: Job
    private var firstEnter = false
    private lateinit var currentUser: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(layoutInflater, container, false)
        getSiteInfo()
        viewModel.upLoad(siteSupervisor, constructionArea)
        observeData("file")
        currentUser = viewModel.currentUser
        setUIForUser()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        saveLocation()
        deleteLocation()
    }

    private fun initView() {
        if (currentLocation?.latitude == 0.0 && currentLocation?.longitude == 0.0) {
            showCurrentLocation()
            infAlert()
            firstEnter = true
        } else {
            showSpecifiedLocation()
        }
    }

    private fun observeData(observedState: String) {
        updateItJob = lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (observedState) {
                    "file" -> {
                        launch {
                            viewModel.upLoadState.collect { upLoadState ->
                                when {
                                    upLoadState.loading -> {
                                        binding.progressBar.show()
                                        binding.constraint.hide()
                                    }

                                    upLoadState.resultList != null -> {
                                        binding.progressBar.hide()
                                        binding.constraint.show()
                                        val latLng = upLoadState.resultList
                                        val location = convertToLatLng(latLng)
                                        currentLocation = location
                                        initView()
                                    }
                                }
                            }
                        }
                    }
                    "save" -> {
                        launch {
                            viewModel.uiState.collect { uiState ->
                                when {
                                    uiState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    uiState.result -> {
                                        toastMessage(
                                            "Konum Başarılı Bir Şekilde Kaydedildi",
                                            requireContext())
                                        firstEnter=false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentLocation() {
        binding.mapView.getMapAsync { googleMap ->
            map = googleMap
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val zoomLevel = 15f // Zoom level (a value between 1 and 20)

                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    currentLocation = currentLatLng
                    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel))

                    // Add a marker
                    val markerOptions =
                        MarkerOptions().position(currentLatLng).title("Şantiye Konumu")
                    map?.addMarker(markerOptions)
                }
            }.addOnFailureListener { exception ->
                // Handle failure to get the last known location here
            }
        }
    }

    private fun showSpecifiedLocation() {
        binding.mapView.getMapAsync { googleMap ->
            map = googleMap
            val zoomLevel = 15f
            val currentLatLng = currentLocation
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, zoomLevel))
            val markerOptions = MarkerOptions().position(currentLatLng!!).title("Şantiye Konumu")
            map?.addMarker(markerOptions)
        }
    }

    private fun convertToLatLng(pair: Pair<String, String>): LatLng {
        val latitude = pair.first.toDoubleOrNull() ?: 0.0
        val longitude = pair.second.toDoubleOrNull() ?: 0.0
        return LatLng(latitude, longitude)
    }

    private fun saveLocation() {

        binding.setLocation.setOnClickListener {
            if (currentLocation?.latitude != 0.0 && currentLocation?.longitude != 0.0) {
                currentLocation?.let {
                    viewModel.saveLocation(currentLocation!!, siteSupervisor, constructionArea)
                    observeData("save")
                }
            } else {
                toastMessage("Konum Seçerken Hata! Daha Sonra Tekrar Deneyiniz", requireContext())
            }
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

    private fun deleteLocation() {

        binding.resetLocation.setOnClickListener {
            if (firstEnter) {
                toastMessage("Lütfen İlk Önce Konum Ekleyiniz", requireContext())
            } else {
                if (currentLocation?.latitude == 0.0 && currentLocation?.longitude != 0.0) {
                    toastMessage("Henüz Konum Eklenmedi.", requireContext())
                } else {
                    val zeroLatLng = LatLng(0.0, 0.0)
                    viewModel.saveLocation(zeroLatLng, siteSupervisor, constructionArea)
                    toastMessage("Konum Başarıyla Silinmiştir", requireContext())
                    navigateFragment()
                }
            }

        }
    }

    private fun navigateFragment() {
        findNavController().navigate(R.id.action_locationFragment_to_settingsFragment)
    }

    private fun infAlert() {
        AestheticDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.INFO)
            .setTitle("Şantiye Konum Bilgisi Henüz Girilmedi").setCancelable(false)
            .setMessage("Eğer Şuanki Konumunuz Şantiye ise Konum Seç Diyerek Konum Ekleyebilirsiniz")
            .setDarkMode(false).setGravity(Gravity.CENTER).setAnimation(DialogAnimation.DEFAULT)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                    //actions...
                }
            }).show()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
    private fun setUIForUser(){
        if (siteSupervisor!=currentUser){
            binding.setLocation.hide()
            binding.textSet.hide()
            binding.textReset.hide()
            binding.resetLocation.hide()
            binding.textInfo.show()
            binding.ivLocation.show()
            binding.icLocation.show()
        }
    }


}