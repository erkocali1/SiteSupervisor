package com.muzo.sitesupervisor.feature.fragment.listingNotes

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.constans.Constants.Companion.KEY_IS_ENTERED
import com.muzo.sitesupervisor.core.constans.Constants.Companion.PREF_NAME
import com.muzo.sitesupervisor.core.data.model.DataModel
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
    private lateinit var sp:SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentListingBinding.inflate(layoutInflater, container, false)
        backPressEvent()
        getConstruction()
        observeData()
        navigateDetailFragment()
        loginEvent()

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
                putString("from", "recyclerview")
                putLong("id", data.id!!)
            }
            findNavController().navigate(R.id.action_listingFragment_to_detailFragment, bundle)
        }
        binding.rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rv.adapter = adapter
    }

    private fun navigateDetailFragment() {
        binding.fabBtn.setOnClickListener {

            val bundleFab = Bundle().apply {
                putString("from", "fab")
            }
            findNavController().navigate(R.id.action_listingFragment_to_detailFragment, bundleFab)
        }
    }

    private fun getConstruction() {
        lifecycleScope.launch {
            val supervisorUserFlow = viewModel.readDataStore("user_key")
            val constructionAreaFlow = viewModel.readDataStore("construction_key")
            val currentUser = viewModel.currentUser

            supervisorUserFlow.collect { supervisorUser ->
                constructionAreaFlow.collect { constructionArea ->
                    Log.d("DataStore super", supervisorUser ?: "null")
                    Log.d("DataStore", constructionArea ?: "null")

                    // Check User or Guest
                    validationUser(currentUser, supervisorUser)

                    if (supervisorUser != null && constructionArea != null) {
                        viewModel.getAllData(supervisorUser, constructionArea)
                        Log.d("bakacaz", "$supervisorUser and $constructionArea")

                    }
                }
            }
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
            id = item.id,
            message = item.message,
            day = item.day,
            title = item.title,
            time = item.time,
            constructionArea = item.constructionArea,
            currentUser = item.currentUser,
            photoUrl = item.photoUrl
        )
    }

    private fun backPressEvent() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            sp= requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor=sp.edit()
            editor.putBoolean(KEY_IS_ENTERED,false)
            editor.apply()
            findNavController().popBackStack(R.id.selectionFragment, false)
        }
    }

    private fun loginEvent(){
        sp= requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor=sp.edit()
        editor.putBoolean(KEY_IS_ENTERED,true)
        editor.apply()
    }


}
