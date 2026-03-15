package com.idz.travelconnect.features.feed

import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.R
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
        binding.tvPostLocation.text = "${post.destination}, ${post.country}"
        binding.tvDates.text = "${post.startDate} - ${post.endDate}"
        binding.tvCommentCount.text = "0 comments"

        if (!post.imageUrl.isNullOrBlank()) {
            binding.ivPostImage.visibility = android.view.View.VISIBLE
            Picasso.get()
                .load(post.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .fit()
                .centerCrop()
                .into(binding.ivPostImage)
        } else {
            binding.ivPostImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        if (!post.userAvatarUrl.isNullOrBlank()) {
            Picasso.get()
                .load(post.userAvatarUrl)
                .placeholder(R.drawable.avatar)
                .fit()
                .centerCrop()
                .into(binding.ivUserAvatar)
        } else {
            binding.ivUserAvatar.setImageResource(R.drawable.avatar)
        }

        binding.root.setOnClickListener { onPostClick(post) }
    }
}