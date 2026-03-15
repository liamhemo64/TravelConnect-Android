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
        binding.tvDestination.text = "${post.destination}, ${post.country}"
        binding.tvDates.text = "${post.startDate} - ${post.endDate}"
        binding.tvUserName.text = post.userName
        binding.tvCommentCount.text = "0 comments" // Placeholder for now

        if (!post.imageUrl.isNullOrBlank()) {
            Picasso.get()
                .load(post.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
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