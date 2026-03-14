package com.idz.travelconnect.features.editpost

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.travelconnect.data.repository.post.PostRepository
import com.idz.travelconnect.model.Post

class EditPostViewModel : ViewModel() {

    private val postRepository = PostRepository.shared

    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()
    val postUpdated = MutableLiveData(false)

    lateinit var post: LiveData<Post?>

    fun init(postId: String) {
        post = postRepository.getPostById(postId)
    }

    fun updatePost(
        original: Post,
        destination: String,
        country: String,
        startDate: String,
        endDate: String,
        description: String,
        newImageBitmap: Bitmap?
    ) {
        if (destination.isBlank() || country.isBlank() || startDate.isBlank() ||
            endDate.isBlank() || description.isBlank()
        ) {
            error.value = "Please fill in all required fields."
            return
        }

        isLoading.value = true
        val updated = original.copy(
            destination = destination.trim(),
            country = country.trim(),
            startDate = startDate,
            endDate = endDate,
            description = description.trim()
        )

        postRepository.updatePost(updated, newImageBitmap) {
            isLoading.value = false
            postUpdated.value = true
        }
    }
}
