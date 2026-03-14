package com.idz.travelconnect.features.feed

import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.databinding.ItemPostBinding
import com.idz.travelconnect.model.Post
import com.squareup.picasso.Picasso

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val onPostClick: (Post) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.tvUserName.text = post.userName
        binding.tvDestination.text = post.destination
        binding.tvDates.text = "${post.startDate} – ${post.endDate}"
        binding.chipCountry.text = post.country

        if (!post.imageUrl.isNullOrBlank()) {
            binding.ivPostImage.visibility = android.view.View.VISIBLE
            Picasso.get()
                .load(post.imageUrl)
                .fit()
                .centerCrop()
                .into(binding.ivPostImage)
        } else {
            binding.ivPostImage.visibility = android.view.View.GONE
        }

        if (!post.userAvatarUrl.isNullOrBlank()) {
            Picasso.get()
                .load(post.userAvatarUrl)
                .fit()
                .centerCrop()
                .into(binding.ivUserAvatar)
        }

        binding.root.setOnClickListener { onPostClick(post) }
    }
}