package com.idz.travelconnect.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_responses")
data class AiResponse(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "",
    val userQuery: String,
    val aiResponse: String,
    val timestamp: Long = System.currentTimeMillis(),
    val flagUrl: String = "",
    val region: String = "",
    val languages: String = "",
    val currencies: String = ""
)
