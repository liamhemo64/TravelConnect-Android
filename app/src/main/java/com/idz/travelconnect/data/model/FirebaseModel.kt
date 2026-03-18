package com.idz.travelconnect.data.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.model.Post

class FirebaseModel {

    private val db = Firebase.firestore

    private companion object COLLECTIONS {
        const val POSTS = "posts"
    }

    // ── Posts ──────────────────────────────────────────────────────────────

    fun getAllPosts(completion: (List<Post>) -> Unit) {
        db.collection(POSTS)
            .get()
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    completion(result.result.map { Post.fromJson(it.data) })
                } else {
                    completion(emptyList())
                }
            }
    }

    fun savePost(post: Post, completion: Completion) {
        db.collection(POSTS)
            .document(post.id)
            .set(post.toJson)
            .addOnSuccessListener { completion() }
            .addOnFailureListener { completion() }
    }

    fun deletePost(postId: String, completion: Completion) {
        db.collection(POSTS)
            .document(postId)
            .delete()
            .addOnSuccessListener { completion() }
            .addOnFailureListener { completion() }
    }
}
