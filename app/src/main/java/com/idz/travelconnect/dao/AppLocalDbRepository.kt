package com.idz.travelconnect.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.idz.travelconnect.model.Post

@Database(entities = [Post::class], version = 5)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract val postDao: PostDao
}
