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

    @Query("SELECT * FROM ai_responses ORDER BY timestamp DESC")
    fun getAllResponses(): LiveData<List<AiResponse>>
}
