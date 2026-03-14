package com.idz.travelconnect.data.repository.post

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.dao.AppLocalDB
import com.idz.travelconnect.data.model.FirebaseModel
import com.idz.travelconnect.model.Post
import java.util.UUID
import java.util.concurrent.Executors

class PostRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val database = AppLocalDB.db
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    companion object {
        val shared = PostRepository()
    }

    fun getAllPosts(): LiveData<List<Post>> = database.postDao.getAllPosts()

    fun getMyPosts(userId: String): LiveData<List<Post>> = database.postDao.getPostsByUser(userId)

    fun getPostById(postId: String): LiveData<Post?> = database.postDao.getPostById(postId)

    fun refreshPosts(completion: Completion = {}) {
        val since = Post.lastSyncTimestamp
        firebaseModel.getAllPosts(since) { posts ->
            executor.execute {
                var latestTime = since
                for (post in posts) {
                    database.postDao.insertPosts(post)
                    post.lastUpdated?.let { if (it > latestTime) latestTime = it }
                }
                if (latestTime > since) Post.lastSyncTimestamp = latestTime
                mainHandler.post { completion() }
            }
        }
    }

    fun createPost(
        userId: String,
        userName: String,
        userAvatarUrl: String?,
        destination: String,
        country: String,
        startDate: String,
        endDate: String,
        description: String,
        imageBitmap: Bitmap?,
        completion: Completion
    ) {
        val postId = UUID.randomUUID().toString()

        fun savePost(imageUrl: String?) {
            val post = Post(
                id = postId,
                userId = userId,
                userName = userName,
                userAvatarUrl = userAvatarUrl,
                destination = destination,
                country = country,
                startDate = startDate,
                endDate = endDate,
                description = description,
                imageUrl = imageUrl,
                lastUpdated = System.currentTimeMillis()
            )
            firebaseModel.savePost(post) {
                executor.execute {
                    database.postDao.insertPosts(post)
                    mainHandler.post { completion() }
                }
            }
        }
    }

    fun updatePost(
        post: Post,
        completion: Completion
    ) {
            val updated = post.copy(imageUrl = post.imageUrl)
            firebaseModel.savePost(updated) {
                executor.execute {
                    database.postDao.insertPosts(updated)
                    mainHandler.post { completion() }
                }
            }
    }

    fun deletePost(postId: String, completion: Completion) {
        firebaseModel.deletePost(postId) {
                executor.execute {
                    database.postDao.deletePost(postId)
                    mainHandler.post { completion() }
                }
        }
    }
}
