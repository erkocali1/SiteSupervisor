package com.muzo.sitesupervisor.feature.fragment.taskFragment

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.addStatusBarColorUpdate
import com.muzo.sitesupervisor.core.common.getColorCompat
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.setTextColorRes
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.databinding.Example3CalendarDayBinding
import com.muzo.sitesupervisor.databinding.Example3CalendarHeaderBinding
import com.muzo.sitesupervisor.databinding.FragmentTaskBinding
import com.muzo.sitesupervisor.feature.adapters.task.TaskAdapter
import com.muzo.sitesupervisor.feature.fragment.baseFragment.BaseFragment
import com.muzo.sitesupervisor.feature.fragment.baseFragment.HasBackButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
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
    private var list: List<TaskModel>? = null
    private var localDateList: List<LocalDate>? = null
    private lateinit var adapter: TaskAdapter
    private val daysOfWeek = daysOfWeek()
    private val currentMonth = YearMonth.now()
    private val startMonth = currentMonth.minusMonths(50)
    private val endMonth = currentMonth.plusMonths(50)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addStatusBarColorUpdate(R.color.example_3_statusbar_color)
        binding = FragmentTaskBinding.bind(view)
        getSiteInfo()
        viewModel.getTaskDate(siteSupervisor, constructionArea)
        observe(ObservedState.DATE_STATE)
        searchEvent()
        config()
        configureToolbarTitle()
        navigateAddTask()

        //  viewModel.getWorker("Boyacılar")
        if (savedInstanceState == null) {
            // Show today's events initially.
            binding.exThreeCalendar.post { selectDate(today) }
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.exThreeCalendar.notifyDateChanged(it) }
            binding.exThreeCalendar.notifyDateChanged(date)
            val selectedDateString = selectedDate?.toString()
            selectedDateString?.let {
                viewModel.getAllTask(siteSupervisor, constructionArea, selectedDateString)
                observe(ObservedState.UI_STATE)
                binding.exThreeSelectedDateText.text = selectionFormatter.format(date)
            }
        }
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

                // Tıklama algılama işlemleri burada gerçekleştirilecek
                container.binding.root.setOnClickListener {
                    if (data.position == DayPosition.MonthDate) {
                        selectDate(data.date)
                        binding.linearLayout.show()
                        binding.list.hide()
                        binding.list.clearFocus()
                    }
                }

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
                            dotView.isVisible = isDateInList(data.date)
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

    override fun onStop() {
        super.onStop()
        activityToolbar.setBackgroundColor(
            requireContext().getColorCompat(R.color.colorPrimary),
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

    private fun observe(observedState: ObservedState) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (observedState) {
                    ObservedState.UI_STATE -> {
                        launch {
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

                    ObservedState.DATE_STATE -> {
                        launch {
                            viewModel.dateState.collect { dateState ->
                                when {
                                    dateState.loading -> {
                                        binding.progressBar.show()
                                        binding.exThreeCalendar.hide()
                                    }

                                    dateState.dateList != null || dateState.isSuccessful -> {
                                        //       config()
                                        binding.progressBar.hide()
                                        dateState.dateList?.let { stringDates ->
                                            localDateList = stringDates.map { dateString ->
                                                convertStringToLocalDate(dateString)
                                            }
                                        }
                                        binding.exThreeCalendar.notifyCalendarChanged()
                                        binding.exThreeCalendar.show()
                                    }
                                }
                            }
                        }
                    }

                    ObservedState.SEARCH_STATE -> {
                        launch {
                            viewModel.workerState.collect { workerState ->
                                when {
                                    workerState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    workerState.resulListWithWorker != null -> {
                                        binding.progressBar.hide()
                                        list = workerState.resulListWithWorker
                                        setupAdapter()
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
        adapter = TaskAdapter(list) {
            navigateByRv(it)
        }
        binding.exThreeRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.exThreeRv.adapter = adapter

    }

    private fun convertStringToLocalDate(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        return LocalDate.parse(dateString, formatter)
    }

    private fun isDateInList(date: LocalDate): Boolean {
        return localDateList?.contains(date) ?: false
    }

    private fun config() {

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

        //  binding.exThreeCalendar.postDelayed({ selectDate(today) }, 250)
        //  binding.exThreeCalendar.post { selectDate(today) }

    }

    private fun navigateAddTask() {
        binding.exThreeAddButton.setOnClickListener {
            val bundle = Bundle().apply {
                val selectedDateString =
                    selectedDate?.format(DateTimeFormatter.ISO_DATE) // Ya da uygun bir format
                putString("selectedDate", selectedDateString)
                putString("location", "addButton")
            }
            findNavController().navigate(R.id.action_taskFragment_to_taskFragmentDetail, bundle)
        }
    }

    private fun navigateByRv(taskModel: TaskModel) {
        val bundle = Bundle().apply {
            putParcelable("sendData", taskModel)
            putString("location", "rv")
        }
        findNavController().navigate(R.id.action_taskFragment_to_taskFragmentDetail, bundle)

    }

    private fun configureToolbarTitle() {
        activityToolbar.title = if (currentMonth.year == today.year) {
            titleSameYearFormatter.format(currentMonth)
        } else {
            titleFormatter.format(currentMonth)
        }
    }

    private fun searchEvent() {
        binding.searchBtn.setOnClickListener {
            list = emptyList()
            setupAdapter()
            binding.linearLayout.visibility = View.INVISIBLE
            binding.list.show()
        }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item,
            com.muzo.sitesupervisor.core.constans.Constants.Companion.ConstructionTeams.TEAMS
        )
        binding.listConstruction.setAdapter(adapter)
        var job: Job? = null
        binding.listConstruction.setOnItemClickListener { _, _, position, _ ->
            val selectedWorker =
                com.muzo.sitesupervisor.core.constans.Constants.Companion.ConstructionTeams.TEAMS[position]
            job?.cancel()
            job = MainScope().launch {
                selectedWorker.let {
                    viewModel.getWorker(selectedWorker)
                    observe(ObservedState.SEARCH_STATE)
                }
            }
        }
    }
}

enum class ObservedState {
    UI_STATE, DATE_STATE, SEARCH_STATE
}



