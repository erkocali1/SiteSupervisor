package com.muzo.sitesupervisor.feature.fragment.taskFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.addStatusBarColorUpdate
import com.muzo.sitesupervisor.core.common.getColorCompat
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.setTextColorRes
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.Event
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.databinding.Example3CalendarDayBinding
import com.muzo.sitesupervisor.databinding.Example3CalendarHeaderBinding
import com.muzo.sitesupervisor.databinding.FragmentTaskBinding
import com.muzo.sitesupervisor.feature.adapters.ListingAdapter
import com.muzo.sitesupervisor.feature.adapters.events.Example3EventsAdapter
import com.muzo.sitesupervisor.feature.adapters.task.TaskAdapter
import com.muzo.sitesupervisor.feature.fragment.baseFragment.BaseFragment
import com.muzo.sitesupervisor.feature.fragment.baseFragment.HasBackButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class TaskFragment : BaseFragment(R.layout.fragment_task), HasBackButton {
    private lateinit var binding: FragmentTaskBinding
    private val viewModel: TaskFragmentViewModel by viewModels()
    override val titleRes: Int = R.string.example_3_title

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private val turkishLocale = Locale("tr", "TR")
    private lateinit var constructionArea: String
    private lateinit var siteSupervisor: String

    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM", turkishLocale)
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy", turkishLocale)
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", turkishLocale)
    private val events = mutableMapOf<LocalDate, List<Event>>()
    private var list: List<TaskModel>? = null
    private lateinit var adapter: TaskAdapter


    private val daysOfWeek = daysOfWeek()
    private val currentMonth = YearMonth.now()
    private val startMonth = currentMonth.minusMonths(50)
    private val endMonth = currentMonth.plusMonths(50)


//    private val eventsAdapter = Example3EventsAdapter {
//        AlertDialog.Builder(requireContext())
//            .setMessage(R.string.example_3_dialog_delete_confirmation)
//            .setPositiveButton(R.string.delete) { _, _ ->
//                deleteEvent(it)
//            }.setNegativeButton(R.string.close, null).show()
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTaskBinding.inflate(layoutInflater, container, false)
        getSiteInfo()


        binding.exThreeAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_taskFragment_to_taskFragmentDetail)
        }

        addStatusBarColorUpdate(R.color.example_3_statusbar_color)

//        binding.exThreeRv.apply {
//            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
//            adapter = eventsAdapter
//            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
//        }

        binding.exThreeCalendar.monthScrollListener = {
            activityToolbar.title = if (it.yearMonth.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }
            // Select the first day of the visible month.
            selectDate(it.yearMonth.atDay(1))
        }


        configureBinders(daysOfWeek)

        binding.exThreeCalendar.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        if (savedInstanceState == null) {
            // Show today's events initially.
            binding.exThreeCalendar.post { selectDate(today) }
        }
//        binding.exThreeAddButton.setOnClickListener { }

        return binding.root

    }


    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.exThreeCalendar.notifyDateChanged(it) }
            binding.exThreeCalendar.notifyDateChanged(date)
            //      updateAdapterForDate(date)

            val selectedDateString = selectedDate?.toString()
            selectedDateString?.let {
                Log.d("getAllTask", selectedDateString)
                viewModel.getAllTask(siteSupervisor, constructionArea, selectedDateString)
                observe()
            }

        }
    }

//    private fun deleteEvent(event: Event) {
//        val date = event.date
//        events[date] = events[date].orEmpty().minus(event)
//        updateAdapterForDate(date)
//    }

    private fun updateAdapterForDate(date: LocalDate) {

        adapter.apply {
            notifyDataSetChanged()
        }
        binding.exThreeSelectedDateText.text = selectionFormatter.format(date)
    }


    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = Example3CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }
        binding.exThreeCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.binding.exThreeDayText
                val dotView = container.binding.exThreeDotView

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.isVisible = true
                    when (data.date) {
                        today -> {
                            textView.setTextColorRes(R.color.example_3_white)
                            textView.setBackgroundResource(R.drawable.example_3_today_bg)
                            dotView.hide()
                        }

                        selectedDate -> {
                            textView.setTextColorRes(R.color.example_3_blue)
                            textView.setBackgroundResource(R.drawable.example_3_selected_bg)
                            dotView.hide()
                        }

                        else -> {
                            textView.setTextColorRes(R.color.example_3_black)
                            textView.background = null
                            dotView.isVisible = events[data.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.hide()
                    dotView.hide()
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = Example3CalendarHeaderBinding.bind(view).legendLayout.root
        }
        binding.exThreeCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].name.take(3).lowercase()
                                    .replaceFirstChar(Char::titlecase)
                                tv.setTextColorRes(R.color.example_3_black)
                            }
                    }
                }
            }
    }

    private fun daysOfWeek(firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): List<DayOfWeek> {
        val pivot = 7 - firstDayOfWeek.ordinal
        val daysOfWeek = DayOfWeek.values()
        return (daysOfWeek.drop(pivot) + daysOfWeek.take(pivot))
    }

    override fun onStart() {
        super.onStart()
        activityToolbar.setBackgroundColor(
            requireContext().getColorCompat(R.color.example_3_toolbar_color),
        )
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

    private fun observe() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {

                        binding.progressBar.show()
                    }

                    uiState.resultList != null -> {
                        binding.progressBar.hide()
                        list = uiState.resultList
                        setupAdapter()
                    }
                }

            }
        }
    }

    private fun setupAdapter() {

        adapter = TaskAdapter(list) {}
        binding.exThreeRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.exThreeRv.adapter = adapter

    }
    private fun getTaskDay(){


    }


}


