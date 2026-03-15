package com.idz.travelconnect.data.repository.post

import android.graphics.Bitmap
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.base.ErrorCompletion
import com.idz.travelconnect.data.model.post.FirebasePostModel
import com.idz.travelconnect.data.model.post.FirebaseStorageModel
import com.idz.travelconnect.data.model.post.Post

class PostRepository private constructor() {

    private val firebasePostModel = FirebasePostModel()
    private val storageModel = FirebaseStorageModel()

    fun addPost(image: Bitmap, post: Post, onSuccess: Completion, onError: ErrorCompletion) {
        storageModel.uploadPostImage(image, post.id) { imageUrl ->
            if (imageUrl == null) {
                onError("Failed to upload image.")
                return@uploadPostImage
            }
            val postWithImage = post.copy(imageUrl = imageUrl)
            firebasePostModel.addPost(postWithImage, onSuccess, onError)
        }
    }

    companion object {
        val shared: PostRepository by lazy { PostRepository() }
    }
}
