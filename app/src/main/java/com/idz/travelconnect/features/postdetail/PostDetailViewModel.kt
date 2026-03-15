package com.idz.travelconnect.features.postdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.data.repository.auth.AuthRepository

import com.idz.travelconnect.data.repository.post.PostRepository

import com.idz.travelconnect.model.Post

class PostDetailViewModel : ViewModel() {

    private val postRepository = PostRepository.shared
    private val authRepository = AuthRepository.shared

    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()
    val postDeleted = MutableLiveData(false)

    private var _postId: String = ""

    lateinit var post: LiveData<Post?>

    fun init(postId: String) {
        _postId = postId
        post = postRepository.getPostById(postId)
    }

    fun deletePost(completion: Completion) {
        isLoading.value = true
        postRepository.deletePost(_postId) {
            isLoading.value = false
            postDeleted.value = true
            completion()
        }
    }

    fun isOwner(post: Post): Boolean {
        val currentId = authRepository.currentUser?.uid
        return currentId != null && post.userId == currentId
    }
}
