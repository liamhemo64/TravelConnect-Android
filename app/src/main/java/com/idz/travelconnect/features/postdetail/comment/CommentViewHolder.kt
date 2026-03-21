package com.idz.travelconnect.features.postdetail.comment

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.databinding.ItemCommentBinding
import com.idz.travelconnect.features.postdetail.comment.CommentsAdapter.Companion.timeFormat
import com.idz.travelconnect.model.Comment
import com.idz.travelconnect.model.User
import com.squareup.picasso.Picasso
import java.util.Date

class CommentViewHolder(
    private val binding: ItemCommentBinding,
    private val currentUserId: String?,
    private val onDeleteClick: (Comment) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: Comment, currentUser: User?) {
        val isOwn = currentUserId != null && comment.userId == currentUserId
        val displayName = if (isOwn && currentUser != null) currentUser.displayName else comment.userName
        val avatarUrl = if (isOwn && currentUser != null) currentUser.avatarUrl else comment.userAvatarUrl

        binding.tvCommentUserName.text = displayName
        binding.tvCommentText.text = comment.text
        binding.tvCommentTime.text = timeFormat.format(Date(comment.timestamp))

        if (!avatarUrl.isNullOrBlank()) {
            Picasso.get()
                .load(avatarUrl)
                .fit()
                .centerCrop()
                .into(binding.ivCommentAvatar)
        }

        if (isOwn) {
            binding.ivDeleteComment.visibility = View.VISIBLE
            binding.ivDeleteComment.setOnClickListener { onDeleteClick(comment) }
        } else {
            binding.ivDeleteComment.visibility = View.GONE
        }
    }
}
