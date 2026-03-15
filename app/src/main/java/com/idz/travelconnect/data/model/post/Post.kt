package com.idz.travelconnect.data.model.post

data class Post(
    val id: String = "",
    val authorId: String = "",
    val location: String = "",
    val caption: String = "",
    val imageUrl: String? = null,
    val lastUpdated: Long? = null
) {
    companion object {
        const val COLLECTION = "posts"
        const val LAST_UPDATED_KEY = "lastUpdated"

        fun fromJson(data: Map<String, Any>): Post {
            return Post(
                id = data["id"] as? String ?: "",
                authorId = data["authorId"] as? String ?: "",
                location = data["location"] as? String ?: "",
                caption = data["caption"] as? String ?: "",
                imageUrl = data["imageUrl"] as? String,
                lastUpdated = data[LAST_UPDATED_KEY] as? Long
            )
        }
    }

    val toJson: Map<String, Any?>
        get() = mapOf(
            "id" to id,
            "authorId" to authorId,
            "location" to location,
            "caption" to caption,
            "imageUrl" to imageUrl,
            LAST_UPDATED_KEY to lastUpdated
        )
}
