package com.idz.travelconnect.data.model.post

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.base.ErrorCompletion

class FirebasePostModel {

    private val db = Firebase.firestore

    fun addPost(post: Post, onSuccess: Completion, onError: ErrorCompletion) {
        db.collection(Post.COLLECTION)
            .document(post.id)
            .set(post.toJson)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to create post.")
            }
    }
}
