package com.muzo.sitesupervisor.feature.adapters.listingimageadapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muzo.sitesupervisor.databinding.ItemImageBinding


class ListingImageAdapter(private val list: List<String>?) :
    RecyclerView.Adapter<ListingImageAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            Log.d("item=>", item)
            Glide.with(itemView.context)
                .load(item)
                .into(binding.ivGalery)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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



