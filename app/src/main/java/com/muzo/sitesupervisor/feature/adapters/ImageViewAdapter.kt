package com.muzo.sitesupervisor.feature.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.feature.adapters.base.BaseListAdapter
import com.muzo.sitesupervisor.feature.adapters.base.ImageViewHolder

class ImageViewAdapter : BaseListAdapter<Uri>(
    itemsSame = { old, new -> old == new },
    contentsSame = { old, new -> old == new }
) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

}

