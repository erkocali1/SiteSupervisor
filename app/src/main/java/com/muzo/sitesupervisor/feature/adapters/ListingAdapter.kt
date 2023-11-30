package com.muzo.sitesupervisor.feature.adapters

import android.content.Context
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
import java.util.Date
import java.util.Locale

class ListingAdapter(private val list: List<DataModel> ,val onClick: (item: DataModel) -> Unit) :
    RecyclerView.Adapter<ListingAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DataModel) {

            binding.apply {
                textTitle.text = item.title
                textDesc.text = item.message
                textTime.text = item.time

                val allDay = separateToDay(item.day)
                Log.d("DAy=>", allDay.toString())
                textDay.text = allDay?.dayOfMonth
                textYear.text = allDay?.year
                dayOfMounth.text = allDay?.day
                textMonth.text = allDay?.month

                val innerRecyclerView: RecyclerView = binding.ivRv
                val layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                innerRecyclerView.layoutManager = layoutManager
                val adapter = ListingImageAdapter(item.photoUrl)
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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")
            try {
                val date = LocalDate.parse(day, formatter)
                val dayOfWeek = date.dayOfWeek.toString()
                val month = date.month.toString()
                val dayOfMonth = date.dayOfMonth.toString()
                val year = date.year.toString()

                Log.d("date=>", "$dayOfWeek,$year,$month,$dayOfMonth")

                DateTimeDetails(dayOfWeek, year, month, dayOfMonth)
            } catch (e: Exception) {
                Log.e("Error", "Date parsing failed", e)
                null
            }
        } else {
            try {
                val date = SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).parse(day)
                val formatter = SimpleDateFormat("EEEE, yyyy, MMMM dd", Locale.getDefault())
                val formattedDate = formatter.format(date ?: Date())
                Log.d("answer", formattedDate)

                null // return null for pre-Oreo versions
            } catch (e: Exception) {
                Log.e("Error", "Date parsing failed", e)
                null
            }
        }
    }

}