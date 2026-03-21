package com.idz.travelconnect.data.repository.post

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.base.ErrorCompletion
import com.idz.travelconnect.dao.AppLocalDB
import com.idz.travelconnect.data.model.FirebaseModel
import com.idz.travelconnect.data.model.StorageModel
import com.idz.travelconnect.model.Post
import java.util.UUID
import java.util.concurrent.Executors

class PostRepository private constructor() {

    private val firebaseModel = FirebaseModel()

    private val storageModel = StorageModel()
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
        val lastUpdated = Post.lastUpdated
        firebaseModel.getAllPosts(lastUpdated) { posts ->
            executor.execute {
                var time = lastUpdated
                for (post in posts) {
                    database.postDao.insertPosts(post)
                    post.lastUpdated?.let { if (time < it) time = it }
                }
                Post.lastUpdated = time

                val userIds = posts.map { it.userId }.distinct()
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

    fun createPost(
        userId: String,
        destination: String,
        country: String,
        startDate: String,
        endDate: String,
        description: String,
        imageBitmap: Bitmap?,
        completion: Completion,
        onError: ErrorCompletion = {}
    ) {
        val postId = UUID.randomUUID().toString()

        fun savePost(imageUrl: String?) {
            val post = Post(
                id = postId,
                userId = userId,
                destination = destination,
                country = country,
                startDate = startDate,
                endDate = endDate,
                description = description,
                imageUrl = imageUrl,
                lastUpdated = System.currentTimeMillis()
            )
            
            // Offline-first: save locally first
            executor.execute {
                database.postDao.insertPosts(post)
                
                // Then sync with Firebase
                firebaseModel.savePost(post) {
                    mainHandler.post { completion() }
                }
            }
        }

        if (imageBitmap != null) {
            storageModel.uploadImage(
                api = StorageModel.StorageAPI.CLOUDINARY,
                folderPath = "posts/$postId",
                image = imageBitmap
            ) { url ->
                if (url != null) {
                    savePost(url)
                } else {
                    mainHandler.post { onError("Failed to upload image. Check your connection.") }
                }
            }
        } else {
            savePost(null)
        }
    }

    fun updatePost(
        post: Post,
        newImageBitmap: Bitmap?,
        completion: Completion
    ) {
        fun saveUpdated(imageUrl: String?) {
            val updated = post.copy(
                imageUrl = imageUrl ?: post.imageUrl,
                lastUpdated = System.currentTimeMillis()
            )
            
            // Offline-first: update locally first
            executor.execute {
                database.postDao.insertPosts(updated)
                
                // Then sync with Firebase
                firebaseModel.savePost(updated) {
                    mainHandler.post { completion() }
                }
            }
        }

        if (newImageBitmap != null) {
            storageModel.uploadImage(
                api = StorageModel.StorageAPI.CLOUDINARY,
                folderPath = "posts/${post.id}",
                image = newImageBitmap
            ) { url -> saveUpdated(url) }
        } else {
            saveUpdated(null)
        }
    }

    fun deletePost(postId: String, completion: Completion) {
        // Offline-first: delete locally first
        executor.execute {
            database.postDao.deletePost(postId)
            database.commentDao.deleteCommentsForPost(postId)
            
            // Then sync with Firebase
            firebaseModel.deletePost(postId) {
                firebaseModel.deleteCommentsForPost(postId) {
                    mainHandler.post { completion() }
                }
            }
        }
    }
}
