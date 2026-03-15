package com.idz.travelconnect.features.post

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.idz.travelconnect.data.model.post.Post
import com.idz.travelconnect.data.repository.post.PostRepository
import java.util.UUID

class PostViewModel : ViewModel() {

    private val postRepository = PostRepository.shared

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _postSuccessData = MutableLiveData<String?>()
    val postSuccessData: LiveData<String?> get() = _postSuccessData

    private var selectedBitmap: Bitmap? = null

    fun onImageSelected(bitmap: Bitmap) {
        selectedBitmap = bitmap
    }

    fun createPost(location: String, caption: String) {
        val bitmap = selectedBitmap
        if (bitmap == null) {
            _error.value = "Please add a photo."
            return
        }
        if (location.isBlank()) {
            _error.value = "Please add a location."
            return
        }
        if (caption.isBlank()) {
            _error.value = "Please write a caption."
            return
        }

        _isLoading.value = true

        val currentUser = Firebase.auth.currentUser
        val post = Post(
            id = UUID.randomUUID().toString(),
            authorId = currentUser?.uid ?: "",
            location = location.trim(),
            caption = caption.trim(),
            imageUrl = null,
            lastUpdated = System.currentTimeMillis()
        )

        postRepository.addPost(
            image = bitmap,
            post = post,
            onSuccess = {
                _isLoading.value = false
                _postSuccessData.value = post.id
            },
            onError = { msg ->
                _isLoading.value = false
                _error.value = msg
            }
        )
    }

    fun clearPostSuccessData() {
        _postSuccessData.value = null
        selectedBitmap = null
    }
}
