package com.muzo.sitesupervisor.feature.adapters.staticsAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muzo.sitesupervisor.core.data.model.WorkInfoModel
import com.muzo.sitesupervisor.databinding.ItemStatisticBinding

class StatisticsAdapter(private var list: List<WorkInfoModel>) :
    RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: ItemStatisticBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WorkInfoModel) {

            binding.apply {
                textDate.text=""
                textVocation.text=item.vocation
                textRecordTime.text=item.operationTime
                textAmount.text=item.operationDuration.toString()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemStatisticBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}