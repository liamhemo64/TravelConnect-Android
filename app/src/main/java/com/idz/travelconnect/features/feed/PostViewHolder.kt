package com.idz.travelconnect.features.feed

import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.databinding.ItemPostBinding
import com.idz.travelconnect.model.Post


class PostViewHolder(
    private val binding: ItemPostBinding,
    private val onPostClick: (Post) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.tvUserName.text = post.userName
        binding.tvDestination.text = post.destination
        binding.tvDates.text = "${post.startDate} – ${post.endDate}"
        binding.tvDescription.text = post.description
        binding.chipCountry.text = post.country

        binding.root.setOnClickListener { onPostClick(post) }
    }
}