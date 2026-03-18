package com.idz.travelconnect.features.postdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.idz.travelconnect.data.repository.auth.AuthRepository
import com.idz.travelconnect.data.repository.comment.CommentRepository

import com.idz.travelconnect.data.repository.post.PostRepository
import com.idz.travelconnect.model.Comment

import com.idz.travelconnect.model.Post

class PostDetailViewModel : ViewModel() {

    private val postRepository = PostRepository.shared

    private val commentRepository = CommentRepository.shared

    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()
    val postDeleted = MutableLiveData(false)

    val currentUser = Firebase.auth.currentUser

    private var _postId: String = ""

    lateinit var post: LiveData<Post?>
    lateinit var comments: LiveData<List<Comment>>

    fun init(postId: String) {
        _postId = postId
        post = postRepository.getPostById(postId)
        comments = commentRepository.getCommentsForPost(postId)
        commentRepository.refreshComments(postId)
    }

    fun deletePost() {
        isLoading.value = true
        postRepository.deletePost(_postId) {
            isLoading.value = false
            postDeleted.value = true
        }
    }

    fun addComment(text: String) {
        if (text.isBlank()) return
        val user = currentUser ?: return

        commentRepository.addComment(
            postId = _postId,
            userId = user.uid,
            userName = user.displayName ?: user.displayName ?: "Traveller",
            userAvatarUrl = user.photoUrl.toString(),
            text = text.trim()
        ) {}
    }

    fun deleteComment(commentId: String) {
        commentRepository.deleteComment(commentId) {}
    }

    fun isOwner(post: Post): Boolean {
        return currentUser?.uid != null && post.userId == currentUser.uid
    }
}
