package com.idz.travelconnect.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.idz.travelconnect.model.AiResponse
import com.idz.travelconnect.model.Post
import com.idz.travelconnect.model.Comment

@Database(entities = [Post::class, Comment::class, AiResponse::class], version = 9)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract val postDao: PostDao

    abstract val commentDao: CommentDao
    abstract val aiResponseDao: AiResponseDao
}
