package com.muzo.sitesupervisor.feature.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.github.dhaval2404.imagepicker.ImagePicker
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomBar()

        binding.icFab.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
    }

    private fun setupBottomBar() {
        val navController = findNavController(R.id.fragmentContainerView)
        NavigationUI.setupWithNavController(binding.bottomMenu, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.listingFragment ->
                    binding.bottomMenu.show()

                else -> binding.bottomMenu.hide()
            }
        }

    }
}