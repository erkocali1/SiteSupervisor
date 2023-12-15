package com.muzo.sitesupervisor.feature.fragment.statisticsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.databinding.FragmentDetailBinding
import com.muzo.sitesupervisor.databinding.FragmentStatisticsBinding


class StatisticsFragment : Fragment() {
    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)



        return binding.root
    }
}