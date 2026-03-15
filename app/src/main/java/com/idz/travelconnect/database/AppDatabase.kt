package com.idz.travelconnect.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.idz.travelconnect.dao.AiResponseDao
import com.idz.travelconnect.model.AiResponse

@Database(entities = [AiResponse::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun aiResponseDao(): AiResponseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "travelconnect_db"
                ).build().also { INSTANCE = it }
            }
    }
}
