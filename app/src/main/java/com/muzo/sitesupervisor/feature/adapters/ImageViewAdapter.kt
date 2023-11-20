package com.muzo.sitesupervisor.feature.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muzo.sitesupervisor.databinding.ItemImageBinding
import com.muzo.sitesupervisor.databinding.ItemRowBinding

class ImageViewAdapter(private val uriList: List<Uri>) :
    RecyclerView.Adapter<ImageViewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = uriList[position]
        holder.bind(currentItem)
    }


    override fun getItemCount(): Int {
        return uriList.size
    }

}