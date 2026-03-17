package com.idz.travelconnect.data.repository.comment

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.dao.AppLocalDB
import com.idz.travelconnect.dao.CommentDao
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
                mainHandler.post { completion() }
            }
        }
    }

    fun addComment(
        postId: String,
        userId: String,
        userName: String,
        userAvatarUrl: String?,
        text: String,
        completion: Completion
    ) {
        val comment = Comment(
            id = UUID.randomUUID().toString(),
            postId = postId,
            userId = userId,
            userName = userName,
            userAvatarUrl = userAvatarUrl,
            text = text,
            timestamp = System.currentTimeMillis(),
            lastUpdated = System.currentTimeMillis()
        )
        firebaseModel.saveComment(comment) {
            executor.execute {
                database.commentDao.insertComments(comment)
                mainHandler.post { completion() }
            }
        }
    }

    fun deleteComment(commentId: String, completion: Completion) {
        firebaseModel.deleteComment(commentId) {
            executor.execute {
                database.commentDao.deleteComment(commentId)
                mainHandler.post { completion() }
            }
        }
    }
}
