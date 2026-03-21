package com.idz.travelconnect.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.idz.travelconnect.model.AiResponse
import com.idz.travelconnect.model.Post
import com.idz.travelconnect.model.User
import com.idz.travelconnect.model.Comment

@Database(entities = [Post::class, Comment::class, AiResponse::class, User::class], version = 12)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract val postDao: PostDao

    abstract val userDao: UserDao

    abstract val commentDao: CommentDao

    abstract val aiResponseDao: AiResponseDao
}
