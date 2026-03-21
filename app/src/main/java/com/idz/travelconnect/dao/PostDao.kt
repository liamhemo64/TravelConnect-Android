package com.idz.travelconnect.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.idz.travelconnect.model.Post

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY lastUpdated DESC")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY lastUpdated DESC")
    fun getPostsByUser(userId: String): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: String): LiveData<Post?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(vararg posts: Post)

    @Query("DELETE FROM posts WHERE id = :postId")
    fun deletePost(postId: String)

    @Query("UPDATE posts SET userName = :userName, userAvatarUrl = :userAvatarUrl WHERE userId = :userId")
    fun updateUserInfo(userId: String, userName: String, userAvatarUrl: String?)
}
