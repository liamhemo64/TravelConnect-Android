package com.idz.travelconnect.features.postdetail.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.idz.travelconnect.databinding.ItemCommentBinding
import com.idz.travelconnect.model.Comment
import com.idz.travelconnect.model.User
import java.text.SimpleDateFormat
import java.util.*

class CommentsAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onDeleteClick: (Comment) -> Unit
) : ListAdapter<Comment, CommentViewHolder>(DIFF_CALLBACK) {

    var currentUser: User? = null

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem
        }
        val timeFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, lifecycleOwner, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position), currentUser)
    }
}
