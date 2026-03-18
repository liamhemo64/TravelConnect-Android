package com.idz.travelconnect.features.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.idz.travelconnect.data.repository.post.PostRepository
import com.idz.travelconnect.model.Post

class FeedViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val postRepository = PostRepository.shared
    private val userId: String? = savedStateHandle.get<String>("userId")

    val posts: LiveData<List<Post>> = if (userId.isNullOrEmpty()) {
        postRepository.getAllPosts()
    } else {
        postRepository.getMyPosts(userId)
    }

    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()

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
