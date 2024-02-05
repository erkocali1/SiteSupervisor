package com.muzo.sitesupervisor.feature.fragment.createAreaFragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.constans.Constants.Companion.ConstructionTeams.TEAMS
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.databinding.FragmentCreateAreaBinding
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.thecode.aestheticdialogs.OnDialogClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAreaFragment : Fragment() {
    private lateinit var binding: FragmentCreateAreaBinding
    private val viewModel: CreateAreFragmentViewModel by viewModels()
    private var postId: Long? = null
    private var list: List<UserConstructionData>? = null
    private var stringList: List<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateAreaBinding.inflate(layoutInflater, container, false)

        infAlert()
        observeData()
        getOtherLoc()
        return binding.root
    }

    private fun observeData() {

        binding.btnCreate.setOnClickListener {

            val constructionNameFirst = binding.etParcelFirst.text.toString()
            val constructionNameSecond = binding.etParcelSecond.text.toString()
            val constructionName = "$constructionNameFirst-$constructionNameSecond"
            val currentUser = viewModel.currentUser?.uid.toString()



            if (constructionNameFirst.isNotEmpty() || constructionNameSecond.isNotEmpty()) {
                if (stringList?.contains(constructionName) == true) {
                    toastMessage("Bu şantiye mevcut lütfen başka bir isim deneyin.")
                } else {

                    val dataModel = createDataModel(constructionName)

                    lifecycleScope.launch {
                        postId = viewModel.saveRoom(dataModel)
                        dataModel.id = postId
                        viewModel.saveArea(dataModel)
                    }

                    lifecycleScope.launch {
                        viewModel.uiState.collect { uiState ->
                            when {

                                uiState.loading -> {
                                    binding.cvWhiteBackground.hide()
                                    binding.cvTop.hide()
                                    binding.progressBar.show()
                                }

                                uiState.isSuccessful -> {

                                    toastMessage(uiState.message!!)
                                    navigateFragment()
                                    viewModel.addItem(currentUser, TEAMS, constructionName)
                                }

                                uiState.message != null -> {
                                    binding.cvWhiteBackground.show()
                                    binding.cvTop.show()
                                    toastMessage(uiState.message.toString())
                                    binding.progressBar.hide()
                                }
                            }
                        }
                    }
                }
            } else {
                toastMessage("Lütfen Tüm Alanları Doldurunuz")
            }
        }
    }

    private fun getOtherLoc() {
        lifecycleScope.launch {
            viewModel.getOtherItemState.collect { getOtherItemState ->
                when {
                    getOtherItemState.loading -> {
                        binding.cvWhiteBackground.hide()
                        binding.cvTop.hide()
                        binding.progressBar.show()
                    }

                    getOtherItemState.resultList != null -> {
                        binding.cvWhiteBackground.show()
                        binding.cvTop.show()
                        list = getOtherItemState.resultList
                        val constructionAreasList: List<String>? =
                            list?.flatMap { it.constructionAreas }?.toList()
                        stringList = constructionAreasList

                    }
                }

            }
        }
    }


    private fun toastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateFragment() {

        val constructionNameFirst = binding.etParcelFirst.text.toString()
        val constructionNameSecond = binding.etParcelSecond.text.toString()
        val constructionName = "$constructionNameFirst-$constructionNameSecond"
        val currentUser = viewModel.currentUser?.uid.toString()

        lifecycleScope.launch {
            viewModel.saveDataStore(currentUser, constructionName)
            findNavController().navigate(R.id.action_createAreaFragment_to_passwordFragment)
        }
    }

    private fun createDataModel(constructionName: String): DataModel {

        val currentUser = viewModel.currentUser?.uid.toString()
        val (currentDate, currentTime) = viewModel.getCurrentDateAndTime()
        return DataModel(
            id = postId,
            message = "Şantiye Defteri Oluşturuldu Günlük Kayıtları Girebilirsiniz",
            title = "Şantiye  Oluşturuldu",
            day = currentDate,
            time = currentTime,
            currentUser = currentUser,
            constructionArea = constructionName
        )
    }

    private fun infAlert() {
        AestheticDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.INFO)
            .setTitle("Şantiye bilgilerini doldurun").setCancelable(false)
            .setMessage("Şantiye bilgilerinizi Ada-Parsel  formata uygun olucak şekilde giriniz")
            .setDarkMode(false).setGravity(Gravity.CENTER).setAnimation(DialogAnimation.DEFAULT)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                    //actions...
                }
            }).show()
    }
}