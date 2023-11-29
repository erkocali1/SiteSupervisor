package com.muzo.sitesupervisor.feature.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.databinding.ItemRowBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ListingAdapter(private val list: List<DataModel>, val onClick: (item: DataModel) -> Unit) :
    RecyclerView.Adapter<ListingAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataModel) {

            binding.apply {
                textTitle.text = item.title
                textDesc.text = item.message
                textTime.text=item.time
                textDay.text=item
                textYear.text
                dayOfMounth.text
                textMonth.text


                root.setOnClickListener {
                    onClick(item)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]
        holder.bind(currentItem)
    }


    override fun getItemCount(): Int {
        return list.size
    }


}