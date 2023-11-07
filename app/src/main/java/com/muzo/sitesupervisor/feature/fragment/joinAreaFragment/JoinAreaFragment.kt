package com.muzo.sitesupervisor.feature.fragment.joinAreaFragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.muzo.sitesupervisor.R
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.aestheticdialogs.OnDialogClickListener

class JoinAreaFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        infAlert()
        return inflater.inflate(R.layout.fragment_join_area, container, false)

    }

    private fun infAlert() {
        AestheticDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.INFO)
            .setTitle("Select the construction site you want to Investigate").setCancelable(false)
            .setMessage("").setDarkMode(false).setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.DEFAULT).setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                    //actions...
                }
            }).show()
    }

}