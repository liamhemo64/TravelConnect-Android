package com.idz.travelconnect.data.repository.comment

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.dao.AppLocalDB
import com.idz.travelconnect.data.model.FirebaseModel
import com.idz.travelconnect.model.Comment
import java.util.UUID
import java.util.concurrent.Executors

class CommentRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val database = AppLocalDB.db
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    companion object {
        val shared = CommentRepository()
    }

    fun getCommentsForPost(postId: String): LiveData<List<Comment>> =
        database.commentDao.getCommentsForPost(postId)

    fun refreshComments(postId: String, completion: Completion = {}) {
        firebaseModel.getCommentsForPost(postId) { comments ->
            executor.execute {
                database.commentDao.insertComments(*comments.toTypedArray())

                val userIds = comments.map { it.userId }.distinct()
                var remaining = userIds.size
                if (remaining == 0) {
                    mainHandler.post { completion() }
                    return@execute
                }
                for (uid in userIds) {
                    firebaseModel.getUserById(uid) { user ->
                        if (user != null) {
                            executor.execute {
                                database.userDao.insertUser(user)
                            }
                        }
                        synchronized(this) {
                            remaining--
                            if (remaining == 0) {
                                mainHandler.post { completion() }
                            }
                        }
                    }
                }
            }
        }
    }

    fun addComment(
        postId: String,
        userId: String,
        text: String,
        completion: Completion
    ) {
        val comment = Comment(
            id = UUID.randomUUID().toString(),
            postId = postId,
            userId = userId,
            text = text,
            timestamp = System.currentTimeMillis(),
            lastUpdated = System.currentTimeMillis()
        )
        
        // Offline-first: Save locally immediately so UI updates instantly
        executor.execute {
            database.commentDao.insertComments(comment)
            
            // Then sync with Firebase in the background
            firebaseModel.saveComment(comment) {
                mainHandler.post { completion() }
            }
        }
    }

    fun deleteComment(commentId: String, completion: Completion) {
        // Offline-first: Delete locally immediately
        executor.execute {
            database.commentDao.deleteComment(commentId)
            
            // Then sync with Firebase
            firebaseModel.deleteComment(commentId) {
                mainHandler.post { completion() }
            }
        }
    }
}
