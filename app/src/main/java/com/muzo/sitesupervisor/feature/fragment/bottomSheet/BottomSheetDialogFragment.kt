package com.muzo.sitesupervisor.feature.fragment.bottomSheet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.databinding.FragmentBottomSheetDialogBinding

class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetDialogBinding.inflate(layoutInflater, container, false)


        return binding.root
    }

}