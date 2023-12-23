package com.muzo.sitesupervisor.feature.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muzo.sitesupervisor.core.data.model.DataModel
import com.muzo.sitesupervisor.core.data.model.DateTimeDetails
import com.muzo.sitesupervisor.databinding.ItemRowBinding
import com.muzo.sitesupervisor.feature.adapters.listingimageadapter.ListingImageAdapter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ListingAdapter(private val list: List<DataModel>, val onClick: (item: DataModel) -> Unit) :
    RecyclerView.Adapter<ListingAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataModel) {

            binding.apply {
                textTitle.text = item.title
                textDesc.text = item.message
                textTime.text = item.time

                val allDay = separateToDay(item.day)
                textDay.text = allDay?.dayOfMonth
                textYear.text = allDay?.year
                dayOfMounth.text = allDay?.day
                textMonth.text = allDay?.month

                val innerRecyclerView: RecyclerView = binding.ivRv
                val layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                innerRecyclerView.layoutManager = layoutManager
                val adapter = ListingImageAdapter(item.photoUrl) {}
                innerRecyclerView.adapter = adapter

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

    fun separateToDay(day: String): DateTimeDetails? {
        return try {
            val formatter = SimpleDateFormat("dd MMMM yyyy EEEE", Locale("tr", "TR"))
            val date = formatter.parse(day)

            val calendar = Calendar.getInstance()
            calendar.time = date ?: Date()

            val dayOfWeek = SimpleDateFormat("EEEE", Locale("tr", "TR")).format(date)
            val month = SimpleDateFormat("MMMM", Locale("tr", "TR")).format(date)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString()
            val year = calendar.get(Calendar.YEAR).toString()

            DateTimeDetails(dayOfWeek, year, month, dayOfMonth)
        } catch (e: Exception) {
            Log.e("Error", "Date parsing failed", e)
            null
        }
    }


}