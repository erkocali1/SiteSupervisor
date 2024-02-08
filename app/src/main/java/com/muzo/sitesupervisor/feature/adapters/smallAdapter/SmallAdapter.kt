package com.muzo.sitesupervisor.feature.adapters.smallAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muzo.sitesupervisor.databinding.ItemImageBinding
import com.muzo.sitesupervisor.databinding.SmallItemImageBinding


class SmallAdapter(private val list: List<String>?,val onClick: (item: String) -> Unit) :
    RecyclerView.Adapter<SmallAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: SmallItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            Log.d("resimLink",item)
            Glide.with(itemView.context)
                .load(item)
                .into(binding.ivGalery)

            binding.root.setOnClickListener{
                onClick(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SmallItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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