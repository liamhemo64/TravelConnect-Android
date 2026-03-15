package com.idz.travelconnect.features.aiassistant

import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.databinding.ItemAiResponseBinding
import com.idz.travelconnect.model.AiResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiResponseViewHolder(private val binding: ItemAiResponseBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AiResponse) {
        binding.tvUserQuery.text = item.userQuery
        binding.tvAiResponse.text = item.aiResponse
        val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        binding.tvTimestamp.text = dateFormat.format(Date(item.timestamp))
    }
}