package com.idz.travelconnect.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.idz.travelconnect.model.Post
import com.idz.travelconnect.model.Comment

@Database(entities = [Post::class, Comment::class], version = 6)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract val postDao: PostDao

    abstract val commentDao: CommentDao
}
