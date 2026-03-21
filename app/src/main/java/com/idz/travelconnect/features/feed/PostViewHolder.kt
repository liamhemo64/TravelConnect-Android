package com.idz.travelconnect.features.feed

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.R
import com.idz.travelconnect.databinding.ItemPostBinding
import com.idz.travelconnect.model.Post
import com.idz.travelconnect.model.User
import com.squareup.picasso.Picasso
import com.idz.travelconnect.data.repository.comment.CommentRepository
import com.idz.travelconnect.data.repository.user.UserRepository
import com.idz.travelconnect.model.Comment

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val onPostClick: (Post) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    private val commentRepository = CommentRepository.shared
    private val userRepository = UserRepository.shared

    private var userLiveData: LiveData<User?>? = null
    private var userObserver: Observer<User?>? = null
    
    private var commentLiveData: LiveData<List<Comment>>? = null
    private var commentObserver: Observer<List<Comment>>? = null

    fun bind(post: Post, currentUser: User?) {
        cleanup()
        resetNonUserUi()

        val ownUser = currentUser?.takeIf { it.uid == post.userId }

        if (ownUser != null) {
            updateUserUi(ownUser)
        } else {
            val liveData = userRepository.getUser(post.userId)
            val observer = object : Observer<User?> {
                override fun onChanged(value: User?) {
                    // Check if this ViewHolder is still showing the same post
                    value?.let { updateUserUi(it) }
                }
            }
            userLiveData = liveData
            userObserver = observer
            liveData.observe(lifecycleOwner, observer)
        }

        binding.tvPostLocation.text = "${post.destination}, ${post.country}"
        binding.tvDates.text = "${post.startDate} - ${post.endDate}"

        // Comments count observer
        val cLiveData = commentRepository.getCommentsForPost(post.id)
        val cObserver = Observer<List<Comment>> { comments ->
            val commentCount = comments?.size ?: 0
            binding.tvCommentCount.text = binding.root.context.getString(R.string.comment_count, commentCount)
        }
        commentLiveData = cLiveData
        commentObserver = cObserver
        cLiveData.observe(lifecycleOwner, cObserver)

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

        binding.root.setOnClickListener { onPostClick(post) }
    }

    private fun resetNonUserUi() {
        binding.tvCommentCount.text = ""
        binding.ivPostImage.setImageResource(android.R.drawable.ic_menu_gallery)
    }

    private fun cleanup() {
        userObserver?.let { userLiveData?.removeObserver(it) }
        commentObserver?.let { commentLiveData?.removeObserver(it) }
        userLiveData = null
        userObserver = null
        commentLiveData = null
        commentObserver = null
    }

    private fun updateUserUi(user: User) {
        binding.tvUserName.text = user.displayName
        if (!user.avatarUrl.isNullOrBlank()) {
            Picasso.get()
                .load(user.avatarUrl)
                .placeholder(R.drawable.avatar)
                .fit()
                .centerCrop()
                .into(binding.ivUserAvatar)
        } else {
            binding.ivUserAvatar.setImageResource(R.drawable.avatar)
        }
    }
}
