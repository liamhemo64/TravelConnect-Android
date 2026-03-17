package com.idz.travelconnect.data.model

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.model.Comment
import com.idz.travelconnect.model.Post

class FirebaseModel {

    private val db = Firebase.firestore

    private companion object COLLECTIONS {
        const val POSTS = "posts"

        const val COMMENTS = "comments"
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

    // ── Comments ───────────────────────────────────────────────────────────

    fun getCommentsForPost(postId: String, completion: (List<Comment>) -> Unit) {
        db.collection(COMMENTS)
            .whereEqualTo(Comment.POST_ID_KEY, postId)
            .get()
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    completion(result.result.map { Comment.fromJson(it.data) })
                } else {
                    completion(emptyList())
                }
            }
    }

    fun saveComment(comment: Comment, completion: Completion) {
        db.collection(COMMENTS)
            .document(comment.id)
            .set(comment.toJson)
            .addOnSuccessListener { completion() }
            .addOnFailureListener { completion() }
    }

    fun deleteComment(commentId: String, completion: Completion) {
        db.collection(COMMENTS)
            .document(commentId)
            .delete()
            .addOnSuccessListener { completion() }
            .addOnFailureListener { completion() }
    }

    fun deleteCommentsForPost(postId: String, completion: Completion) {
        db.collection(COMMENTS)
            .whereEqualTo(Comment.POST_ID_KEY, postId)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { batch.delete(it.reference) }
                batch.commit()
                    .addOnSuccessListener { completion() }
                    .addOnFailureListener { completion() }
            }
            .addOnFailureListener { completion() }
    }

}
