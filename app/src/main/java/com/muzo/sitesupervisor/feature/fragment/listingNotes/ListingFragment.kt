package com.muzo.sitesupervisor.feature.fragment.listingNotes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.UserConstructionData
import com.muzo.sitesupervisor.databinding.FragmentListingBinding
import com.muzo.sitesupervisor.feature.adapters.ListingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListingFragment : Fragment() {
    private val viewModel: ListingFramentViewModel by viewModels()
    private lateinit var binding: FragmentListingBinding
    private lateinit var adapter: ListingAdapter
    private lateinit var list: List<DataModel>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentListingBinding.inflate(layoutInflater, container, false)

        getConstruction()
        observeData()
        navigateDetailFragment()


        return binding.root
    }

    private fun observeData() {

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when {
                    uiState.loading -> {
                        binding.progressBar.show()
                    }

                    uiState.resultList != null -> {
                        toastMessage(uiState.message.toString())
                        binding.progressBar.hide()
                        list = uiState.resultList
                        setupAdapter()

                    }
                }
            }
        }
    }

    private fun setupAdapter() {

        adapter = ListingAdapter(list) { data ->
            val postList = bind(data)
            val bundle = Bundle().apply {
                putParcelable("dataList", postList)
            }
            findNavController().navigate(R.id.action_listingFragment_to_detailFragment, bundle)
        }
        binding.rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rv.adapter = adapter
    }

    private fun toastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateDetailFragment() {
        binding.fabBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listingFragment_to_detailFragment)

        }
    }

    private fun getConstruction() {
        val userConstructionData =
            arguments?.getParcelable<UserConstructionData>("userConstructionData")
        val supervisorUser = userConstructionData?.currentUser
        val constructionArea = userConstructionData?.constructionAreas
        val currentUser = viewModel.currentUser

        //checkUser or Guest
        validationUser(currentUser, supervisorUser)

        val constructionAreaAsString = constructionArea?.joinToString(", ")

        if (supervisorUser != null && constructionAreaAsString != null) {
            viewModel.getData(supervisorUser, constructionAreaAsString)
            Log.d("bakacaz", "$supervisorUser and $constructionAreaAsString")
        }

    }

    private fun validationUser(currentUser: String, superVisorUser: String?) {
        if (currentUser == superVisorUser) {
            binding.fabBtn.show()
        } else {
            binding.fabBtn.hide()
        }
    }

    private fun bind(item: DataModel): DataModel {
        return DataModel(
            message = item.message,
            day = item.day,
            title = item.title,
            time = item.time,
            constructionArea = item.constructionArea,
            currentUser = item.currentUser,
            photoUrl = item.photoUrl
        )
    }
}
