package com.muzo.sitesupervisor.feature.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomBar()
        setupAppBar()

    }

    private fun setupBottomBar() {
        val navController = findNavController(R.id.fragmentContainerView)
        NavigationUI.setupWithNavController(binding.bottomMenu, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.listingFragment, R.id.statisticsFragment, R.id.taskFragment,R.id.settingsFragment -> binding.bottomMenu.show()

                else -> binding.bottomMenu.hide()
            }
        }

    }

    private fun setupAppBar() {
        val navController = findNavController(R.id.fragmentContainerView)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.taskFragment -> binding.activityToolbar.show()

                else -> binding.activityToolbar.hide()
            }
        }

    }

}