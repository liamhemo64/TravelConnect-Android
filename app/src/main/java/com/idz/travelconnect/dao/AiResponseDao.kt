package com.idz.travelconnect.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.idz.travelconnect.model.AiResponse

@Dao
interface AiResponseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: AiResponse)

    @Query("SELECT * FROM ai_responses WHERE userId = :userId ORDER BY timestamp DESC")
    fun getResponsesByUser(userId: String): LiveData<List<AiResponse>>
}
