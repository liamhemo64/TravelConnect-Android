package com.idz.travelconnect.data.model

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.model.Post
import com.idz.travelconnect.model.User

class FirebaseModel {

    private val db = Firebase.firestore

    private companion object COLLECTIONS {
        const val POSTS = "posts"
        const val USERS = "users"
    }

    // ── Posts ──────────────────────────────────────────────────────────────

    fun getAllPosts(since: Long, completion: (List<Post>) -> Unit) {
        db.collection(POSTS)
            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED_KEY, Timestamp(since / 1000, 0))
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

    // ── Users ──────────────────────────────────────────────────────────────

    fun saveUser(user: User, completion: Completion) {
        db.collection(USERS)
            .document(user.uid)
            .set(user.toJson)
            .addOnSuccessListener { completion() }
            .addOnFailureListener { completion() }
    }

    fun getUserById(uid: String, completion: (User?) -> Unit) {
        db.collection(USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    completion(User.fromJson(doc.data ?: emptyMap()))
                } else {
                    completion(null)
                }
            }
            .addOnFailureListener { completion(null) }
    }
}
