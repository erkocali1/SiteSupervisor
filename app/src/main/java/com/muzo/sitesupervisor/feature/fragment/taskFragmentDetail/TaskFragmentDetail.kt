package com.muzo.sitesupervisor.feature.fragment.taskFragmentDetail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.databinding.FragmentTaskDetailBinding
import com.muzo.sitesupervisor.feature.adapters.textAdapter.SmallTextAdapter
import com.muzo.sitesupervisor.feature.adapters.textAdapter.TextAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@AndroidEntryPoint

class TaskFragmentDetail : Fragment() {
    private lateinit var binding: FragmentTaskDetailBinding
    private val viewModel: TaskFragmentDetailViewModel by viewModels()
    private var stringList: MutableList<String>? = mutableListOf()
    private lateinit var adapter: SmallTextAdapter
    private var taskIdNumber: Long? = null
    private lateinit var constructionArea: String
    private var location: String? = null
    private lateinit var siteSupervisor: String
    private var specifiedDay: String? = null
    private val turkishLocale = Locale("tr", "TR")
    private var dataEmpty: Boolean = false
    private var sendData: Boolean = false
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", turkishLocale)
    private lateinit var workerTeamList: List<String>
    private lateinit var currentUser: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTaskDetailBinding.inflate(layoutInflater, container, false)

        defineToLocate()
        getSiteInfo()
        viewModel.getTeam(siteSupervisor, constructionArea)
        observeData("getITem")
        currentUser = viewModel.currentUser
        sendData()
        validationUser()
        backButtonEvent()
        backStackEvent()
        deletePostEvent()

        return binding.root
    }

    private fun observeData(observedState: String) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (observedState) {
                    "getITem" -> {
                        launch {
                            viewModel.getTaskTeamState.collect { getTaskTeamState ->
                                when {
                                    getTaskTeamState.loading -> {

                                    }

                                    getTaskTeamState.resultList != null -> {
                                        workerTeamList = getTaskTeamState.resultList
                                        setList()
                                    }
                                }
                            }
                        }
                    }

                    "saveTask" -> {
                        launch {
                            viewModel.uiState.collect { uiState ->
                                when {
                                    uiState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    uiState.isSaveTask && sendData -> {
                                        binding.progressBar.hide()
                                        navigateFragment()
                                    }
                                }
                            }
                        }
                    }

                    "deleteTask" -> {
                        launch {
                            viewModel.deleteTaskState.collect { deleteTaskState ->
                                when {
                                    deleteTaskState.loading -> {
                                        binding.progressBar.show()
                                    }

                                    deleteTaskState.isDelete -> {
                                        binding.progressBar.hide()
                                        navigateFragment()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun defineToLocate() {
        location = arguments?.getString("location")
        if (location == "addButton") {
            showDate()
        } else {
            showData()
        }
    }

    private fun showData() {
        val receivedData = arguments?.getParcelable<TaskModel>("sendData")
        specifiedDay = receivedData?.day
        val convertStringToLocalDate = LocalDate.parse(specifiedDay, DateTimeFormatter.ISO_DATE)
        taskIdNumber = receivedData?.taskId

        binding.time.text = convertStringToLocalDate.format(selectionFormatter)
        binding.etDes.setText(receivedData?.message)
        binding.etTitle.setText(receivedData?.title)
        stringList = receivedData?.workerList?.toMutableList()
        if (stringList?.isNotEmpty() == true) {
            setupAdapter()
        }
    }

    private fun setList() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, workerTeamList)
        binding.listConstruction.setAdapter(adapter)

        binding.listConstruction.setOnItemClickListener { _, _, position, _ ->
            val selectedConstruction = workerTeamList[position]
            val selectedList = listOf(selectedConstruction)
            stringList?.addAll(selectedList)
            setupAdapter()
            toastMessage(
                "$selectedConstruction listeye eklendi", requireContext()
            )
        }
    }

    private fun setupAdapter() {
        adapter = SmallTextAdapter(stringList) {
            if (siteSupervisor == currentUser) {
                deleteFromList(it)
            }
        }
        binding.rvWorkerPicker.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvWorkerPicker.adapter = adapter

    }

    private fun deleteFromList(item: String) {
        stringList?.remove(item)
        toastMessage("$item kaldırıldı", requireContext())
        adapter.notifyDataSetChanged()
    }


    private fun getData(): TaskModel? {
        val title = binding.etTitle.text.toString()
        val desc = binding.etDes.text.toString()
        val workerList = stringList
        val date = specifiedDay


        return if (title.isNotBlank() || desc.isNotBlank() || workerList?.isNotEmpty() == true) {
            dataEmpty=false
            TaskModel(
                taskIdNumber, desc, title, date!!, workerList, siteSupervisor, constructionArea
            )

        } else {
            dataEmpty = true
            null
        }
    }

    private fun sendData() {
        binding.ivButtonOK.setOnClickListener {
            getData()
            if (dataEmpty) {
                toastMessage("Lütfen bilgilerinizi Doldurunuz", requireContext())
            } else {
                if (location == "rv") {
                    if (changeListener()) {
                        val data = getData()
                        lifecycleScope.launch {
                            viewModel.saveRoom(data!!)
                            viewModel.saveTask(data)
                            sendData = true
                            observeData("saveTask")
                        }
                    } else {
                        navigateFragment()
                    }
                } else {
                    val data = getData()
                    lifecycleScope.launch {
                        taskIdNumber = viewModel.saveRoom(data!!)
                        data.taskId = taskIdNumber
                        viewModel.saveTask(data.copy(taskId = taskIdNumber))
                        sendData = true
                        observeData("saveTask")
                    }
                }
            }
        }
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

    private fun navigateFragment() {
        findNavController().navigate(R.id.action_taskFragmentDetail_to_taskFragment)
    }

    private fun showDate() {
        specifiedDay = arguments?.getString("selectedDate")
        val convertStringToLocalDate = LocalDate.parse(specifiedDay, DateTimeFormatter.ISO_DATE)
        binding.time.text = convertStringToLocalDate.format(selectionFormatter)
    }

    private fun changeListener(): Boolean {
        val oldItem = arguments?.getParcelable<TaskModel>("sendData")
        val currentStringList = stringList
        val currentDesc = binding.etDes.text.toString()
        val currentTitle = binding.etTitle.text.toString()
        return oldItem?.title != currentTitle || oldItem.message != currentDesc || oldItem.workerList != currentStringList
    }

    private fun backButtonEvent() {
        binding.back.setOnClickListener {
            navigateFragment()
        }
    }

    private fun validationUser() {
        if (currentUser != siteSupervisor) {
            binding.okBtn.hide()
            binding.okDelete.hide()
            binding.etDes.isEnabled = false
            binding.etTitle.isEnabled = false
            binding.list.visibility = View.GONE
            binding.rvWorkerPicker.isEnabled = false
        }
    }

    private fun backStackEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // D fragmentından B fragmentına kadar olan tüm fragmentları geri al
            findNavController().popBackStack(R.id.taskFragment, false)
        }
    }

    private fun showDeleteConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Görev Sil")
        alertDialogBuilder.setMessage("Bu görev silindiğinde bu kayıda ait tüm veriler silinicektir.Onaylıyor musunuz?")
        alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
            deleteEvent()
        }
        alertDialogBuilder.setNegativeButton("Hayır") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deletePostEvent() {
        binding.okDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }
    private fun deleteEvent(){
        viewModel.deleteTask(siteSupervisor,constructionArea,specifiedDay!!,taskIdNumber.toString())
        observeData("deleteTask")
    }

}