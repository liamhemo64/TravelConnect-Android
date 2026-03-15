package com.idz.travelconnect.data.model

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.idz.travelconnect.base.StringCompletion
import java.io.ByteArrayOutputStream

class FirebaseStorageModel {

    private val storage = Firebase.storage

    fun uploadImage(folderPath: String, image: Bitmap, completion: StringCompletion) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("$folderPath/${System.currentTimeMillis()}.jpg")
        uploadImageToRef(image, imageRef, completion)
    }

    private fun uploadImageToRef(image: Bitmap, ref: StorageReference, completion: StringCompletion) {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val data = baos.toByteArray()

        ref.putBytes(data)
            .addOnFailureListener { completion(null) }
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { uri -> completion(uri.toString()) }
                    .addOnFailureListener { completion(null) }
            }
    }
}
