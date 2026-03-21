package com.idz.travelconnect.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey val id: String,
    val postId: String,
    val userId: String,
    val text: String,
    val timestamp: Long,
    val lastUpdated: Long?
) {
    companion object {
        const val ID_KEY = "id"
        const val POST_ID_KEY = "postId"
        const val USER_ID_KEY = "userId"
        const val TEXT_KEY = "text"
        const val TIMESTAMP_KEY = "timestamp"
        const val LAST_UPDATED_KEY = "lastUpdated"

        fun fromJson(json: Map<String, Any?>): Comment {
            val lastUpdatedTs = json[LAST_UPDATED_KEY] as? Timestamp
            val ts = json[TIMESTAMP_KEY] as? Long ?: System.currentTimeMillis()
            return Comment(
                id = json[ID_KEY] as? String ?: "",
                postId = json[POST_ID_KEY] as? String ?: "",
                userId = json[USER_ID_KEY] as? String ?: "",
                text = json[TEXT_KEY] as? String ?: "",
                timestamp = ts,
                lastUpdated = lastUpdatedTs?.toDate()?.time
            )
        }
    }

    val toJson: Map<String, Any?>
        get() = hashMapOf(
            ID_KEY to id,
            POST_ID_KEY to postId,
            USER_ID_KEY to userId,
            TEXT_KEY to text,
            TIMESTAMP_KEY to timestamp,
            LAST_UPDATED_KEY to FieldValue.serverTimestamp()
        )
}
