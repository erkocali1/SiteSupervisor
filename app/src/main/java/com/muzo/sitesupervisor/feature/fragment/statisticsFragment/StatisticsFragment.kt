package com.muzo.sitesupervisor.feature.fragment.statisticsFragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import com.muzo.sitesupervisor.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private val viewModel: SaveStatisticViewModel by viewModels()
    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var barList: ArrayList<BarEntry>
    private lateinit var barDataSet: BarDataSet
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String
    private lateinit var barData: BarData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)


        binding.bttn.setOnClickListener {
            findNavController().navigate(R.id.action_statisticsFragment_to_bottomSheetDialogFragment)
        }
        initViews()
        getSiteInfo()
        viewModel.getStatisticForVocation(siteSupervisor,constructionArea,"Demirci")
        return binding.root
    }

    private fun initViews() {
        setPieChartData(12, 100F)
        setData()
    }

    private fun setData() {
        val chart = binding.barChart
        val months = arrayOf(
            "Ocak",
            "Şubat",
            "Mart",
            "Nisan",
            "Mayıs",
            "Haziran",
            "Temmuz",
            "Ağustos",
            "Eylül",
            "Ekim",
            "Kasım",
            "Aralık",
        )

        barList = ArrayList()
        barList.add(BarEntry(0f, 10f))
        barList.add(BarEntry(1f, 20f))
        barList.add(BarEntry(2f, 30f))
        barList.add(BarEntry(3f, 40f))
        barList.add(BarEntry(4f, 50f))
        barList.add(BarEntry(5f, 60f))
        barList.add(BarEntry(6f, 70f))
        barList.add(BarEntry(7f, 80f))
        barList.add(BarEntry(8f, 90f))
        barList.add(BarEntry(9f, 100f))
        barList.add(BarEntry(10f, 110f))
        barList.add(BarEntry(11f, 120f))


        barDataSet = BarDataSet(barList, "Aylar")

        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
        barDataSet.valueTextSize = 15f
        barData = BarData(barDataSet)
        chart.data = barData

        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        chart.invalidate()

    }

    private fun setPieChartData(count: Int, range: Float) {
        val entries = ArrayList<PieEntry>()
        val months = arrayOf(
            "Ocak",
            "Şubat",
            "Mart",
            "Nisan",
            "Mayıs",
            "Haziran",
            "Temmuz",
            "Ağustos",
            "Eylül",
            "Ekim",
            "Kasım",
            "Aralık",
        )

        for (i in 0 until count) {
            entries.add(
                PieEntry(
                    ((Math.random() * range + range / 5).toFloat()),
                    months[i % months.size]
                )
            )
        }

        val dataSet = PieDataSet(entries, "Election Results")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val colors = ArrayList<Int>()

        colors.addAll(ColorTemplate.VORDIPLOM_COLORS.asList())
        colors.addAll(ColorTemplate.JOYFUL_COLORS.asList())
        colors.addAll(ColorTemplate.COLORFUL_COLORS.asList())
        colors.addAll(ColorTemplate.LIBERTY_COLORS.asList())
        colors.addAll(ColorTemplate.PASTEL_COLORS.asList())
        colors.add(ColorTemplate.getHoloBlue())

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        binding.pieChart.data = data
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

}

//        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
//            override fun onValueSelected(e: Entry?, h: Highlight?) {
//                if (e != null) {
//                    val xValue = e.x.toInt()
//                    val month = months.getOrNull(xValue)
//                    if (month != null) {
//                        toastMessage(month, requireContext())
//
//                    }
//                }
//            }
//
//            override fun onNothingSelected() {
//                onNothingSelected()
//            }
//
//
//        })
/*
private fun setData() {
    val chart = binding.barChart
    val months = arrayOf(
        "Ocak",
        "Şubat",
        "Mart",
        "Nisan",
        "Mayıs",
        "Haziran",
        "Temmuz",
        "Ağustos",
        "Eylül",
        "Ekim",
        "Kasım",
        "Aralık",
    )

    barList = ArrayList()
    for (i in months.indices) {
        val value = (i + 1) * 10 // Örnek değer hesaplaması, isteğe bağlı
        val entry = BarEntry(i.toFloat(), value.toFloat()) // X olarak ay ismi, Y olarak örnek bir değer
        barList.add(entry)
    }

    barDataSet = BarDataSet(barList, "Aylar")
    barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
    barDataSet.valueTextSize = 15f
    barData = BarData(barDataSet)
    chart.data = barData

    val xAxis = chart.xAxis
    xAxis.valueFormatter = IndexAxisValueFormatter(months)
    xAxis.position = XAxis.XAxisPosition.BOTTOM

    chart.invalidate()
}*/
