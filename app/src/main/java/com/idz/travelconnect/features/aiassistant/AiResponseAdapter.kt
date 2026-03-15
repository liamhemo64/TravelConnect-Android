package com.idz.travelconnect.features.aiassistant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.databinding.ItemAiResponseBinding
import com.idz.travelconnect.model.AiResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiResponseAdapter : ListAdapter<AiResponse, AiResponseAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemAiResponseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AiResponse) {
            binding.tvUserQuery.text = item.userQuery
            binding.tvAiResponse.text = item.aiResponse
            val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
            binding.tvTimestamp.text = dateFormat.format(Date(item.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAiResponseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AiResponse>() {
        override fun areItemsTheSame(oldItem: AiResponse, newItem: AiResponse) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AiResponse, newItem: AiResponse) =
            oldItem == newItem
    }
}
