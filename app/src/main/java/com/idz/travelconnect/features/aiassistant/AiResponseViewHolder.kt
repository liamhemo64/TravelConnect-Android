package com.idz.travelconnect.features.aiassistant

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.databinding.ItemAiResponseBinding
import com.idz.travelconnect.model.AiResponse
import com.squareup.picasso.Picasso
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

        if (item.flagUrl.isNotBlank()) {
            binding.countryInfoSection.visibility = View.VISIBLE
            binding.tvCountryName.text = item.userQuery.substringBefore(" - ")
            binding.tvRegion.text = "Region: ${item.region}"
            binding.tvLanguage.text = "Language: ${item.languages}"
            binding.tvCurrency.text = "Currency: ${item.currencies}"
            Picasso.get().load(item.flagUrl).into(binding.ivFlag)
        } else {
            binding.countryInfoSection.visibility = View.GONE
        }
    }
}