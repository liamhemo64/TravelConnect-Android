package com.idz.travelconnect.features.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.travelconnect.data.repository.post.PostRepository
import com.idz.travelconnect.model.Post

class FeedViewModel : ViewModel() {

    private val postRepository = PostRepository.shared

    val posts: LiveData<List<Post>> = postRepository.getAllPosts()
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
