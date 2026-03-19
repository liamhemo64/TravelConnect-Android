package com.idz.travelconnect.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.idz.travelconnect.model.Post
import com.idz.travelconnect.model.User

@Database(entities = [Post::class, User::class], version = 10)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract val postDao: PostDao

    abstract val userDao: UserDao
}
