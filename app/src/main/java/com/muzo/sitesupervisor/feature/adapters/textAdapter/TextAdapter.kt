package com.muzo.sitesupervisor.feature.adapters.textAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muzo.sitesupervisor.databinding.ItemTeamBinding

class TextAdapter(private var list: List<String>?, val onClick: (item: String) -> Unit) : RecyclerView.Adapter<TextAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.teamName.text = item
            binding.icDelete.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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