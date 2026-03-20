package com.idz.travelconnect.features.post

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.travelconnect.data.repository.auth.AuthRepository
import com.idz.travelconnect.data.repository.post.PostRepository
import com.idz.travelconnect.data.repository.user.UserRepository

class PostViewModel : ViewModel() {

    private val authRepository = AuthRepository.shared
    private val userRepository = UserRepository.shared
    private val postRepository = PostRepository.shared

    private val uid = authRepository.currentUser?.uid ?: ""
    val user = userRepository.getUser(uid)

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

        val appUser = user.value
        postRepository.createPost(
            userId = uid,
            userName = appUser?.displayName ?: "",
            userAvatarUrl = appUser?.avatarUrl,
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
