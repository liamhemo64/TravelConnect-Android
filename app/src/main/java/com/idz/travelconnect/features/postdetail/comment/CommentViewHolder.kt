package com.idz.travelconnect.features.postdetail.comment

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.idz.travelconnect.R
import com.idz.travelconnect.databinding.ItemCommentBinding
import com.idz.travelconnect.features.postdetail.comment.CommentsAdapter.Companion.timeFormat
import com.idz.travelconnect.model.Comment
import com.idz.travelconnect.model.User
import com.idz.travelconnect.data.repository.user.UserRepository
import com.squareup.picasso.Picasso
import java.util.Date

class CommentViewHolder(
    private val binding: ItemCommentBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val onDeleteClick: (Comment) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val userRepository = UserRepository.shared
    
    private var userLiveData: LiveData<User?>? = null
    private var userObserver: Observer<User?>? = null

    fun bind(comment: Comment, currentUser: User?) {
        cleanup()
        
        val ownUser = currentUser?.takeIf { it.uid == comment.userId }
        
        if (ownUser != null) {
            updateUserUi(ownUser)
        } else {
            val liveData = userRepository.getUser(comment.userId)
            val observer = Observer<User?> { user ->
                user?.let { updateUserUi(it) }
            }
            userLiveData = liveData
            userObserver = observer
            liveData.observe(lifecycleOwner, observer)
        }

        binding.tvCommentText.text = comment.text
        binding.tvCommentTime.text = timeFormat.format(Date(comment.timestamp))

        if (ownUser != null) {
            binding.ivDeleteComment.visibility = View.VISIBLE
            binding.ivDeleteComment.setOnClickListener { onDeleteClick(comment) }
        } else {
            binding.ivDeleteComment.visibility = View.GONE
        }
    }
    
    
    private fun cleanup() {
        userObserver?.let { userLiveData?.removeObserver(it) }
        userLiveData = null
        userObserver = null
    }

    private fun updateUserUi(user: User) {
        binding.tvCommentUserName.text = user.displayName
        if (!user.avatarUrl.isNullOrBlank()) {
            Picasso.get()
                .load(user.avatarUrl)
                .placeholder(R.drawable.avatar)
                .fit()
                .centerCrop()
                .into(binding.ivCommentAvatar)
        } else {
            binding.ivCommentAvatar.setImageResource(R.drawable.avatar)
        }
    }
}
