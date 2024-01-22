package com.muzo.sitesupervisor.feature.fragment.bottomSheet

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel
import com.muzo.sitesupervisor.databinding.FragmentBottomSheetDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar


@AndroidEntryPoint
class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetDialogBinding
    private val viewModel: BottomSheetDialogViewModel by viewModels()
    private var selectedDate = ""
    private var specifiedMonth = ""
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private lateinit var updateItJob: Job
    private lateinit var workerTeamList: List<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetDialogBinding.inflate(layoutInflater, container, false)
        setDatePicker()
        getSiteInfo()
        viewModel.getTeam(siteSupervisor, constructionArea)
        observeData("getTeam")
        sendData()

        return binding.root
    }

    private fun setList() {
        val adapter = ArrayAdapter(
            requireContext(), R.layout.list_item, workerTeamList
        )
        binding.listConstruction.setAdapter(adapter)

        binding.listConstruction.setOnItemClickListener { _, _, position, _ ->
            val selectedConstruction = workerTeamList[position]
        }
    }

    private fun setDatePicker() {
        val dayPicker = binding.etStartDay

        dayPicker.setOnClickListener {
            hideKeyboard(dayPicker)
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            val monthsArray = arrayOf(
                "Ocak", "Şubat", "Mart", "Nisan",
                "Mayıs", "Haziran", "Temmuz", "Ağustos",
                "Eylül", "Ekim", "Kasım", "Aralık"
            )

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { datePicker: DatePicker, selectedYear: Int, selectedMonth: Int, day: Int ->
                    val selectedMonthName = monthsArray[selectedMonth] // Seçilen ayı al
                    selectedDate = "$day $selectedMonthName $selectedYear"
                    specifiedMonth = selectedMonthName
                    dayPicker.setText(selectedDate)
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }
    }


    private fun hideKeyboard(et: EditText) {
        val imm = et.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et.windowToken, 0)
    }

    private fun sendData() {
        binding.saveBtn.setOnClickListener {
            if (isCheckBlankItem()) {
                val data = getData()
                viewModel.saveStatistic(data)
                observeData("file")
            } else {
                toastMessage("Lütfen Tüm Alanları Doldurunuz", requireContext())
            }
        }
    }

    private fun observeData(observedState: String) {
        updateItJob = lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (observedState) {
                    "file" -> {
                        launch {
                            viewModel.uiState.collect { uiState ->
                                when {
                                    uiState.isSuccessful -> {
                                        binding.progressBar.hide()
                                        clearFields()
                                        toastMessage(
                                            "Kayıt Başarı Bir Şekilde Eklenmiştir",
                                            requireContext()
                                        )
                                        updateItJob.cancel()
                                    }

                                    uiState.loading -> {
                                        binding.progressBar.show()
                                    }
                                }
                            }
                        }
                    }

                    "getTeam" -> {
                        launch {
                            viewModel.getTeamBottomState.collect { getTeamBottomState ->
                                when {
                                    getTeamBottomState.loading -> {

                                    }

                                    getTeamBottomState.resultList != null -> {
                                        workerTeamList = getTeamBottomState.resultList
                                        setList()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isCheckBlankItem(): Boolean {
        val listItem = binding.listConstruction.text.toString()
        val dayItem = selectedDate
        val workDayItem = binding.etFinishDay.text.toString()
        val cost = binding.etCoastMoney.text.toString()
        val amountPaid = binding.etAvailableBalance.text.toString()

        return listItem.isNotBlank() && dayItem.isNotBlank() && workDayItem.isNotBlank() && cost.isNotBlank() && amountPaid.isNotBlank()
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

    private fun getData(): WorkInfoModel {
        val listItem = binding.listConstruction.text.toString()
        val dayItem = selectedDate
        val workDayItem = binding.etFinishDay.text.toString().toLong()
        val cost = binding.etCoastMoney.text.toString()
        val amountPaid = binding.etAvailableBalance.text.toString()

        return WorkInfoModel(
            listItem,
            dayItem,
            workDayItem,
            siteSupervisor,
            constructionArea,
            specifiedMonth,
            cost,
            amountPaid
        )
    }

    private fun clearFields() {
        binding.listConstruction.setText("")
        binding.etStartDay.setText("")
        binding.etFinishDay.setText("")
        binding.etCoastMoney.setText("")
        binding.etAvailableBalance.setText("")
    }
}


