package com.muzo.sitesupervisor.feature.adapters.textAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.muzo.sitesupervisor.databinding.ItemWorkerBinding

class TextAdapter(private var list: List<String>?,val onClick: (item: String) -> Unit) :
    RecyclerView.Adapter<TextAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemWorkerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.workerName.text=item
            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWorkerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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