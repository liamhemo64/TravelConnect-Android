package com.idz.travelconnect.features.aiassistant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.idz.travelconnect.databinding.ItemAiResponseBinding
import com.idz.travelconnect.model.AiResponse

class AiResponseAdapter : ListAdapter<AiResponse, AiResponseViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AiResponseViewHolder {
        val binding = ItemAiResponseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AiResponseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AiResponseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AiResponse>() {
        override fun areItemsTheSame(oldItem: AiResponse, newItem: AiResponse) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AiResponse, newItem: AiResponse) =
            oldItem == newItem
    }
}
