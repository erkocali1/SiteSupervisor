package com.muzo.sitesupervisor.feature.adapters.image

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.muzo.sitesupervisor.databinding.ItemImageBinding

class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemImageBinding.bind(view)

    fun bind(uri: Uri) = with(binding) {
        ivGalery.setImageURI(uri)
    }

}