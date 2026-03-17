package com.idz.travelconnect.features.postdetail.comment

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.features.postdetail.comment.CommentsAdapter.Companion.timeFormat
import com.idz.travelconnect.model.Comment
import com.squareup.picasso.Picasso
import java.util.Date

class CommentViewHolder(private val binding: ItemCommentBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: Comment) {
        binding.tvCommentUserName.text = comment.userName
        binding.tvCommentText.text = comment.text
        binding.tvCommentTime.text = timeFormat.format(Date(comment.timestamp))

        if (!comment.userAvatarUrl.isNullOrBlank()) {
            Picasso.get()
                .load(comment.userAvatarUrl)
                .fit()
                .centerCrop()
                .into(binding.ivCommentAvatar)
        }

        if (comment.userId == currentUserId) {
            binding.ivDeleteComment.visibility = View.VISIBLE
            binding.ivDeleteComment.setOnClickListener { onDeleteClick(comment) }
        } else {
            binding.ivDeleteComment.visibility = View.GONE
        }
    }
}
