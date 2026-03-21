package com.idz.travelconnect.features.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.idz.travelconnect.databinding.ItemPostBinding
import com.idz.travelconnect.model.Post
import com.idz.travelconnect.model.User


class PostsAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onPostClick: (Post) -> Unit,
    private val onDeleteClick: ((Post) -> Unit)? = null
) : ListAdapter<Post, PostViewHolder>(DIFF_CALLBACK) {

    var currentUser: User? = null

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, lifecycleOwner, onPostClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), currentUser)
    }
}
