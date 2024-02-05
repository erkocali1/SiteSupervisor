package com.muzo.sitesupervisor.feature.fragment.deleteAreaFragment

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.databinding.FragmentDeleteAreaBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DeleteAreaFragment : Fragment() {
    private lateinit var binding: FragmentDeleteAreaBinding
    private val viewModel: DeleteAreaViewModel by viewModels()
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private lateinit var sp: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeleteAreaBinding.inflate(layoutInflater, container, false)



        getSiteInfo()
        deleteEvent()


        return binding.root
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

    private fun deleteData() {
        lifecycleScope.launch {
            viewModel.deleteAreaState.collect { deleteAreaState ->
                when {
                    deleteAreaState.loading -> {
                       binding.cvTop.hide()
                       binding.cvWorker.hide()
                        binding.progressBar.show()
                    }

                    deleteAreaState.result -> {
                        binding.progressBar.hide()
                        delInfoForStart()
                    }
                }
            }
        }
    }

    private fun deleteEvent() {
        binding.delBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("$constructionArea Şantiyesini Sil")
        builder.setMessage("Bu şantiye alanını silmek istediğinize emin misiniz?")
        builder.setPositiveButton("Evet") { dialog, _ ->
            // Kullanıcının Evet'e tıklaması durumunda yapılacak işlemler
            viewModel.deleteArea(siteSupervisor, constructionArea)
            deleteData()
            dialog.dismiss()
        }
        builder.setNegativeButton("Hayır") { dialog, _ ->
            // Kullanıcının Hayır'a tıklaması durumunda yapılacak işlemler
            dialog.dismiss()
        }
        builder.show()
    }


    private fun delInfoForStart() {
        toastMessage(
            "$constructionArea şantiyesi ve şantiye kayıtları silindi",
            requireContext()
        )
        sp = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean(Constants.KEY_IS_ENTERED, false)
        editor.apply()
        findNavController().popBackStack(R.id.selectionFragment, false)
    }

}