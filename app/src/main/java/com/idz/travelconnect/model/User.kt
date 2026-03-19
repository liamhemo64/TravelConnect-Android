package com.idz.travelconnect.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val lastUpdated: Long?
) {
    companion object {
        const val UID_KEY = "uid"
        const val DISPLAY_NAME_KEY = "displayName"
        const val EMAIL_KEY = "email"
        const val AVATAR_URL_KEY = "avatarUrl"
        const val LAST_UPDATED_KEY = "lastUpdated"

        fun fromJson(json: Map<String, Any?>): User {
            val timestamp = json[LAST_UPDATED_KEY] as? Timestamp
            return User(
                uid = json[UID_KEY] as? String ?: "",
                displayName = json[DISPLAY_NAME_KEY] as? String ?: "",
                email = json[EMAIL_KEY] as? String ?: "",
                avatarUrl = json[AVATAR_URL_KEY] as? String,
                lastUpdated = timestamp?.toDate()?.time
            )
        }
    }

    val toJson: Map<String, Any?>
        get() = hashMapOf(
            UID_KEY to uid,
            DISPLAY_NAME_KEY to displayName,
            EMAIL_KEY to email,
            AVATAR_URL_KEY to avatarUrl,
            LAST_UPDATED_KEY to FieldValue.serverTimestamp()
        )
}
