package com.muzo.sitesupervisor.feature.adapters.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.TaskModel
import com.muzo.sitesupervisor.databinding.ItemImageBinding
import com.muzo.sitesupervisor.databinding.TaskEventsBinding
import com.muzo.sitesupervisor.feature.adapters.listingimageadapter.ListingImageAdapter
import com.muzo.sitesupervisor.feature.adapters.textAdapter.SmallTextAdapter
import com.muzo.sitesupervisor.feature.adapters.textAdapter.TextAdapter

class TaskAdapter(private val list: List<TaskModel>?, val onClick: (item: TaskModel) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: TaskEventsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TaskModel) {
            binding.apply {
                textTitle.text = item.title
                textDesc.text = item.message
                textTime.text = item.day

                val innerRecyclerView: RecyclerView = binding.workerRv
                val layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                innerRecyclerView.layoutManager = layoutManager
               val adapter = SmallTextAdapter(item.workerList) {}
                innerRecyclerView.adapter = adapter

                root.setOnClickListener {
                    onClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TaskEventsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.let {
            val item = it[position]
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }


}