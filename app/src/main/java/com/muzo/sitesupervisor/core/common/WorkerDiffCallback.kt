package com.muzo.sitesupervisor.core.common

import androidx.recyclerview.widget.DiffUtil

class WorkerDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem // Implement your logic for identifying if items are the same
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem // Implement your logic for comparing item contents
    }
}
