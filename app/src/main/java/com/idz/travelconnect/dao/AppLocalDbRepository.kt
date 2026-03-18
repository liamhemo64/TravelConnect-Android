package com.idz.travelconnect.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.idz.travelconnect.model.AiResponse
import com.idz.travelconnect.model.Post

@Database(entities = [Post::class, AiResponse::class], version = 8)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract val postDao: PostDao
    abstract val aiResponseDao: AiResponseDao
}
