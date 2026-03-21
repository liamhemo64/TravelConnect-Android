package com.idz.travelconnect.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.idz.travelconnect.model.Comment

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: String): LiveData<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComments(vararg comments: Comment)

    @Query("DELETE FROM comments WHERE id = :commentId")
    fun deleteComment(commentId: String)

    @Query("DELETE FROM comments WHERE postId = :postId")
    fun deleteCommentsForPost(postId: String)

    @Query("UPDATE comments SET userName = :userName, userAvatarUrl = :userAvatarUrl WHERE userId = :userId")
    fun updateUserInfo(userId: String, userName: String, userAvatarUrl: String?)
}
