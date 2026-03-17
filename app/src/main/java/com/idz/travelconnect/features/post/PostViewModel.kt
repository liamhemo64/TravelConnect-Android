package com.idz.travelconnect.features.post

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.idz.travelconnect.data.repository.post.PostRepository

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

    fun createPost(destination: String, country: String, startDate: String, endDate: String, description: String) {
        if (selectedBitmap == null) {
            _error.value = "Please add a photo."
            return
        }
        if (destination.isBlank()) {
            _error.value = "Please add a destination."
            return
        }
        if (country.isBlank()) {
            _error.value = "Please add a country."
            return
        }
        if (startDate.isBlank() || endDate.isBlank()) {
            _error.value = "Please add travel dates."
            return
        }
        if (description.isBlank()) {
            _error.value = "Please write a description."
            return
        }

        _isLoading.value = true

        val currentUser = Firebase.auth.currentUser
        postRepository.createPost(
            userId = currentUser?.uid ?: "",
            userName = currentUser?.displayName ?: "",
            userAvatarUrl = currentUser?.photoUrl?.toString(),
            destination = destination.trim(),
            country = country.trim(),
            startDate = startDate.trim(),
            endDate = endDate.trim(),
            description = description.trim(),
            imageBitmap = selectedBitmap,
            completion = {
                _isLoading.value = false
                _postSuccessData.value = "done"
            },
            onError = { errorMsg ->
                _isLoading.value = false
                _error.value = errorMsg
            }
        )
    }

    fun clearPostSuccessData() {
        _postSuccessData.value = null
        selectedBitmap = null
    }
}
