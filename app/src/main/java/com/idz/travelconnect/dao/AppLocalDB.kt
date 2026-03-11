package com.idz.travelconnect.dao

import androidx.room.Room
import com.idz.travelconnect.TravelConnectApplication

object AppLocalDB {

    val db: AppLocalDbRepository by lazy {

        val context = TravelConnectApplication.appContext
            ?: throw IllegalStateException("Context is null")

        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "travelconnect.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }
}
