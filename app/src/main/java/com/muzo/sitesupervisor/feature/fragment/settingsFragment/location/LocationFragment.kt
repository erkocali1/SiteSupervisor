package com.muzo.sitesupervisor.feature.fragment.settingsFragment.location

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.LocationUtility
import com.muzo.sitesupervisor.core.constans.Constants.Companion.REQUEST_CODE_LOCATION_PERMISSION
import com.muzo.sitesupervisor.databinding.FragmentLocationBinding
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class LocationFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentLocationBinding
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestPermissions()
        binding = FragmentLocationBinding.inflate(layoutInflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        mapview()
    }

    private fun mapview() {
        binding.mapView.getMapAsync { googleMap ->
            map = googleMap
            // Harita hazır olduğunda yapılacak işlemler burada olmalı
            // Belirli bir konuma gitmek için:
            val latitude = 41.0 // Belirli bir enlem
            val longitude = 29.0 // Belirli bir boylam
            val zoomLevel = 15f // Yakınlaştırma seviyesi (1 ile 20 arasında bir değer)

            val location = LatLng(latitude, longitude)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))

            // İşaretçi eklemek için:
            val markerOptions = MarkerOptions().position(location).title("İşaretçi Başlığı")
            map?.addMarker(markerOptions)
        }
    }



    private fun requestPermissions() {
        if (LocationUtility.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "Bu uygulamayı kullanmak icin konum izinine ihtiyaç vardır",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Bu uygulamayı kullanmak icin konum izinine ihtiyaç vardır",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()

        } else {
            requestPermissions()
        }
    }



    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState)
    }


}