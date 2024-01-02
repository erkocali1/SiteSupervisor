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
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.databinding.FragmentBottomSheetDialogBinding
import java.util.Calendar


class BottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetDialogBinding
    private var selectedDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetDialogBinding.inflate(layoutInflater, container, false)
        setDatePicker()
        setList()
        return binding.root
    }

    private fun setList() {
        val adapter = ArrayAdapter(
            requireContext(),
            com.muzo.sitesupervisor.R.layout.list_item,
            Constants.Companion.ConstructionTeams.TEAMS
        )
        binding.listConstruction.setAdapter(adapter)

        binding.listConstruction.setOnItemClickListener { _, _, position, _ ->
            val selectedConstruction = Constants.Companion.ConstructionTeams.TEAMS[position]
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

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { datePicker: DatePicker, selectedYear: Int, selectedMonth: Int, day: Int ->
                    // Seçilen tarihi EditText'e yaz ve değişkene atayın
                    selectedDate = "$day-${selectedMonth + 1}-$selectedYear"
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


}

