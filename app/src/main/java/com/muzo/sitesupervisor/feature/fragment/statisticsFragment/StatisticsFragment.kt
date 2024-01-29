package com.muzo.sitesupervisor.feature.fragment.statisticsFragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel
import com.muzo.sitesupervisor.databinding.FragmentStatisticsBinding
import com.muzo.sitesupervisor.feature.adapters.staticsAdapter.StatisticsAdapter
import com.muzo.sitesupervisor.feature.adapters.task.TaskAdapter
import com.muzo.sitesupervisor.feature.fragment.taskFragment.ObservedState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okio.utf8Size
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private val viewModel: SaveStatisticViewModel by viewModels()
    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private var list: List<WorkInfoModel> = emptyList()
    private lateinit var adapter: StatisticsAdapter
    private lateinit var updateItJob: Job
    private lateinit var workerTeamList: List<String>
    private lateinit var currentUser: String
    private lateinit var vocation: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)


        navigateToBottomSheet()
        getSiteInfo()
        viewModel.getTeam(siteSupervisor, constructionArea)
        observeData("getTeam")
        currentUser = viewModel.currentUser

        setFragmentResultListener("saveResult") { _, bundle ->
            val saved = bundle.getBoolean("saved", false)
            val savedVocation = bundle.getString("savedVocation", "Kalıpçılar")
            if (saved) {
                viewModel.getStatisticForVocation(siteSupervisor, constructionArea, savedVocation!!)
                observeData("file")
            }
        }
        validationUser()
        return binding.root
    }


    private fun setBarChartData(months: List<String>, operationDuration: List<Float>) {
        val chart = binding.barChart
        chart.description.text = "Aylık Çalışma Miktarları(Gün)"
        val monthlyTotalDuration = mutableMapOf<String, Float>()

        // Aylara göre çalışma sürelerini topla veya ekle
        for (i in months.indices) {
            val month = months[i]
            val duration = operationDuration[i]

            // Aynı ay için daha önce bir giriş yapılmadıysa ekle, yapıldıysa topla
            if (monthlyTotalDuration.containsKey(month)) {
                val currentTotal = monthlyTotalDuration[month] ?: 0f
                monthlyTotalDuration[month] = currentTotal + duration
            } else {
                monthlyTotalDuration[month] = duration
            }
        }

        // Birleştirilmiş aylar ve toplam süreler için veri listesi oluştur
        val combinedMonths = mutableListOf<String>()
        val combinedDurations = mutableListOf<Float>()

        for (entry in monthlyTotalDuration) {
            combinedMonths.add(entry.key)
            combinedDurations.add(entry.value)
        }

        // Yeni oluşturulan verileri kullanarak çubuk grafik veri setlerini oluştur
        val barList = ArrayList<BarEntry>()
        for (i in combinedMonths.indices) {
            barList.add(BarEntry(i.toFloat(), combinedDurations[i]))
        }

        val barDataSet = BarDataSet(barList, "Aylar")
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
        barDataSet.valueTextSize = 15f
        val barData = BarData(barDataSet)
        chart.data = barData

        // X ekseninde ayların yazısını etiketleme
        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(combinedMonths)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelCount = combinedMonths.size // Tüm ayların gösterilmesini sağlar

        val yAxis = chart.axisLeft
        yAxis.axisMinimum = 0f


        // Grafik güncellemesi
        chart.invalidate()
    }

    private fun setPieChartData(cost: List<Float>, amountPaid: List<Float>) {
        // Toplam maliyet ve ödenen miktarı hesapla
        val totalCost = cost.sum()
        val totalAmountPaid = amountPaid.sum()

        // Farkı hesapla
        val remainingAmount = totalCost - totalAmountPaid

        // Verileri hazırla
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(totalCost, "Toplam Maliyet"))
        entries.add(PieEntry(totalAmountPaid, "Ödenen Miktar"))
        entries.add(PieEntry(remainingAmount, "Kalan Miktar"))


        val colors = intArrayOf(
            Color.parseColor("#F9E0BB"), // Turuncu
            Color.parseColor("#607D8B"), // Yeşil
            Color.parseColor("#C38154")  // Mavi
        )

        // Veri setini oluştur
        val dataSet = PieDataSet(entries, "|Miktarlar")
        dataSet.colors = colors.toList()
        dataSet.valueTextSize = 13f
        dataSet.valueTextColor = Color.BLACK

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)

        binding.pieChart.data = data
        binding.pieChart.setEntryLabelColor(Color.BLACK)
        binding.pieChart.description.text = "Toplam Alacak Verecek Miktarı"
        binding.pieChart.highlightValues(null)
        binding.pieChart.invalidate()
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
    private fun observeData(observedState: String) {
        updateItJob = lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (observedState) {
                    "file" -> {
                        launch {
                            viewModel.uiState.collect { uiState ->
                                when {
                                    uiState.loading -> {
                                        binding.cvPie.hide()
                                        binding.cvBarChart.hide()
                                        binding.rvRecords.hide()
                                        binding.textLayout.hide()
                                        binding.progressBar.show()
                                    }
                                    uiState.resultList != null -> {
                                        binding.progressBar.hide()
                                        list = uiState.resultList!!
                                        if (list.isEmpty()) {
                                            updateViewVisibility(false)
                                        } else {
                                            updateViewVisibility(true)
                                            val monthList = extractTime(list)
                                            val operationDuration = extractWokDay(list)
                                            val cost = extractCost(list)
                                            val amountPaid = extractAmountPaid(list)
                                            setupAdapter()
                                            setBarChartData(monthList, operationDuration)
                                            setPieChartData(cost, amountPaid)
                                            uiState.resultList= null
                                            updateItJob.cancel()
                                        }
                                    }
                                }
                            }
                        }
                    }
                    "getTeam" -> {
                        launch {
                            viewModel.teamStatisticState.collect { teamStatisticState ->
                                when {
                                    teamStatisticState.loading -> {
                                        binding.progressBar.show()
                                        binding.allLayout.hide()
                                    }

                                    teamStatisticState.resultList != null -> {
                                        binding.progressBar.hide()
                                        binding.allLayout.show()
                                        workerTeamList = teamStatisticState.resultList
                                        setList()
                                    }
                                }
                            }
                        }
                    }
                    "deleteEvent" -> {
                        launch {
                            viewModel.deleteState.collect { deleteState ->
                                when {
                                    deleteState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    deleteState.isDelete  -> {
                                        binding.progressBar.hide()
                                        viewModel.getStatisticForVocation(siteSupervisor, constructionArea, vocation)
                                        observeData("file")
                                        deleteState.isDelete=false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun setupAdapter() {

        adapter = StatisticsAdapter(list){
            showDeleteConfirmationDialog(vocation, randomId =it)
        }

        binding.rvRecords.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvRecords.adapter = adapter

    }

    private fun setList() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.custom_spinner_item,
            workerTeamList
        )
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.getStatisticForVocation(siteSupervisor, constructionArea, selectedItem)
                observeData("file")
                vocation=selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Hiçbir şey seçilmediğinde ne olacağını belirleyebilirsiniz
            }


        }
    }

    private fun extractTime(workInfoModel: List<WorkInfoModel>): List<String> {

        val operationTimes = mutableListOf<String>()
        for (workInfo in workInfoModel) {
            operationTimes.add(workInfo.specifiedMonth)
        }
        return operationTimes
    }

    private fun extractWokDay(workInfoModel: List<WorkInfoModel>): List<Float> {
        val operationDuration = mutableListOf<Float>()

        for (workDuration in workInfoModel) {
            operationDuration.add(workDuration.operationDuration.toFloat())
        }
        return operationDuration
    }

    private fun extractCost(workInfoModel: List<WorkInfoModel>): List<Float> {
        val operationCost = mutableListOf<Float>()
        for (cost in workInfoModel) {
            operationCost.add(cost.cost.toFloat())
        }
        return operationCost
    }

    private fun extractAmountPaid(workInfoModel: List<WorkInfoModel>): List<Float> {
        val operationAmountPaid = mutableListOf<Float>()
        for (amountPaid in workInfoModel) {
            operationAmountPaid.add(amountPaid.amountPaid.toFloat())
        }
        return operationAmountPaid
    }

    private fun navigateToBottomSheet() {
        binding.bttn.setOnClickListener {
            findNavController().navigate(R.id.action_statisticsFragment_to_bottomSheetDialogFragment)
        }
    }

    private fun validationUser() {
        if (currentUser != siteSupervisor) {
            binding.bttn.hide()
        }
    }
    private fun updateViewVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.cvBarChart.show()
            binding.cvPie.show()
            binding.rvRecords.show()
            binding.ivEmpty.hide()
            binding.textEmpty.hide()
            binding.textLayout.show()
        } else {
            binding.cvBarChart.hide()
            binding.cvPie.hide()
            binding.rvRecords.hide()
            binding.ivEmpty.show()
            binding.textEmpty.show()
            binding.textLayout.hide()
        }
    }

    private fun deleteStatistic(infoVocation:String, randomId:String){
        viewModel.deleteStatistic(siteSupervisor,constructionArea,infoVocation,randomId)
        observeData("deleteEvent")
    }

    private fun showDeleteConfirmationDialog(infoVocation:String, randomId:String) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Kaydı Sil")
        alertDialogBuilder.setMessage("Bu kaydı silmek istiyor musunuz?")
        alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
            deleteStatistic(infoVocation, randomId)
        }
        alertDialogBuilder.setNegativeButton("Hayat") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}



