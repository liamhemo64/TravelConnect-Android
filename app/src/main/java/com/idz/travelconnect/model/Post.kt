package com.idz.travelconnect.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.idz.travelconnect.TravelConnectApplication

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String,
    val userId: String,
    val destination: String,
    val country: String,
    val startDate: String,
    val endDate: String,
    val description: String,
    val imageUrl: String?,
    val lastUpdated: Long?
) {
    companion object {

        var lastUpdated: Long
            get() {
                return TravelConnectApplication.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(LAST_UPDATED_KEY, 0) ?: 0
            }
            set(value) {
                TravelConnectApplication.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.edit()
                    ?.putLong(LAST_UPDATED_KEY, value)
                    ?.apply()
            }

        const val ID_KEY = "id"
        const val USER_ID_KEY = "userId"
        const val DESTINATION_KEY = "destination"
        const val COUNTRY_KEY = "country"
        const val START_DATE_KEY = "startDate"
        const val END_DATE_KEY = "endDate"
        const val DESCRIPTION_KEY = "description"
        const val IMAGE_URL_KEY = "imageUrl"
        const val LAST_UPDATED_KEY = "lastUpdated"

        fun fromJson(json: Map<String, Any?>): Post {
            val timestamp = json[LAST_UPDATED_KEY] as? Timestamp
            return Post(
                id = json[ID_KEY] as? String ?: "",
                userId = json[USER_ID_KEY] as? String ?: "",
                destination = json[DESTINATION_KEY] as? String ?: "",
                country = json[COUNTRY_KEY] as? String ?: "",
                startDate = json[START_DATE_KEY] as? String ?: "",
                endDate = json[END_DATE_KEY] as? String ?: "",
                description = json[DESCRIPTION_KEY] as? String ?: "",
                imageUrl = json[IMAGE_URL_KEY] as? String,
                lastUpdated = timestamp?.toDate()?.time
            )
        }
    }

    val toJson: Map<String, Any?>
        get() = hashMapOf(
            ID_KEY to id,
            USER_ID_KEY to userId,
            DESTINATION_KEY to destination,
            COUNTRY_KEY to country,
            START_DATE_KEY to startDate,
            END_DATE_KEY to endDate,
            DESCRIPTION_KEY to description,
            IMAGE_URL_KEY to imageUrl,
            LAST_UPDATED_KEY to FieldValue.serverTimestamp()
        )
}
