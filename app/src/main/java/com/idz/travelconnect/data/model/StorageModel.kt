package com.idz.travelconnect.data.model

import android.graphics.Bitmap
import com.idz.travelconnect.base.StringCompletion

class StorageModel {

    enum class StorageAPI {
        FIREBASE,
        CLOUDINARY
    }

    private val firebaseStorage = FirebaseStorageModel()
    private val cloudinaryStorage = CloudinaryStorageModel()

    fun uploadImage(
        api: StorageAPI = StorageAPI.CLOUDINARY,
        folderPath: String,
        image: Bitmap,
        completion: StringCompletion
    ) {
        when (api) {
            StorageAPI.FIREBASE -> firebaseStorage.uploadImage(folderPath, image, completion)
            StorageAPI.CLOUDINARY -> cloudinaryStorage.uploadImage(folderPath, image, completion)
        }
    }
}
