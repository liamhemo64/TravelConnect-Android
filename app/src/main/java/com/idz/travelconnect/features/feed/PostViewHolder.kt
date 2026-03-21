package com.idz.travelconnect.features.feed

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.R
import com.idz.travelconnect.databinding.ItemPostBinding
import com.idz.travelconnect.model.Post
import com.idz.travelconnect.model.User
import com.squareup.picasso.Picasso
import com.idz.travelconnect.data.repository.comment.CommentRepository

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val onPostClick: (Post) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private val commentRepository = CommentRepository.shared

    fun bind(post: Post, currentUser: User?) {
        val isOwn = currentUser != null && post.userId == currentUser.uid

        binding.tvUserName.text = if (isOwn) currentUser!!.displayName else post.userName
        binding.tvPostLocation.text = "${post.destination}, ${post.country}"
        binding.tvDates.text = "${post.startDate} - ${post.endDate}"

        // Observe comment count reactively
        commentRepository.getCommentsForPost(post.id).observe(lifecycleOwner) { comments ->
            val commentCount = comments?.size ?: 0
            binding.tvCommentCount.text = binding.root.context.getString(R.string.comment_count, commentCount)
        }

        // Trigger a refresh of comments for this post to ensure the count is up to date
        commentRepository.refreshComments(post.id)

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

        val avatarUrl = if (isOwn) currentUser!!.avatarUrl else post.userAvatarUrl
        if (!avatarUrl.isNullOrBlank()) {
            Picasso.get()
                .load(avatarUrl)
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
