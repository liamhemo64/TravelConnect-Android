package com.idz.travelconnect.data.model.post

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.idz.travelconnect.base.StringCompletion
import java.io.ByteArrayOutputStream

class FirebaseStorageModel {

    private val storage = Firebase.storage

    fun uploadPostImage(image: Bitmap, postId: String, completion: StringCompletion) {
        val ref = storage.reference.child("images/posts/$postId/image.jpg")
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
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
