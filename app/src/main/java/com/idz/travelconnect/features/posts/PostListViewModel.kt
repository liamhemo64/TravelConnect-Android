package com.idz.travelconnect.features.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.idz.travelconnect.data.repository.auth.AuthRepository
import com.idz.travelconnect.data.repository.post.PostRepository
import com.idz.travelconnect.data.repository.user.UserRepository
import com.idz.travelconnect.model.Post

class PostListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val postRepository = PostRepository.shared
    private val authRepository = AuthRepository.shared
    private val userRepository = UserRepository.shared

    // Safely get userId from arguments
    private val userId: String? = savedStateHandle.get<String>("userId")

    private val uid = authRepository.currentUser?.uid ?: ""
    val currentAppUser = userRepository.getUser(uid)

    val posts: LiveData<List<Post>> = if (userId.isNullOrEmpty()) {
        postRepository.getAllPosts()
    } else {
        postRepository.getMyPosts(userId)
    }

    val isLoading = MutableLiveData(false)

    init {
        refresh()
    }

    fun refresh() {
        isLoading.value = true
        postRepository.refreshPosts {
            isLoading.value = false
        }
    }
}
