package com.idz.travelconnect.features.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.databinding.ItemPostBinding
import com.idz.travelconnect.model.Post

class PostsAdapter(
    private val onPostClick: (Post) -> Unit,
    private val onDeleteClick: ((Post) -> Unit)? = null
) : ListAdapter<Post, PostsAdapter.PostViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
        }
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
