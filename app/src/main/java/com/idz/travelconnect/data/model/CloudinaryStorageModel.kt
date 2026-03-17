package com.idz.travelconnect.data.model

import android.content.Context
import android.graphics.Bitmap
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import com.cloudinary.android.policy.UploadPolicy
import com.idz.travelconnect.TravelConnectApplication
import com.idz.travelconnect.base.StringCompletion
import com.idz.travelconnect.base.cloudinaryConfig
import java.io.File

class CloudinaryStorageModel {

    init {
        try {
            TravelConnectApplication.appContext?.let {
                MediaManager.init(it, cloudinaryConfig)
                MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.Builder()
                    .maxConcurrentRequests(3)
                    .networkPolicy(UploadPolicy.NetworkType.ANY)
                    .build()
            }
        } catch (_: Exception) {
            // Already initialized
        }
    }

    fun uploadImage(folderPath: String, image: Bitmap, completion: StringCompletion) {
        val context = TravelConnectApplication.appContext ?: run { completion(null); return }
        val file = bitmapToFile(image, context)

        MediaManager.get().upload(file.path)
            .option("folder", folderPath)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    completion(url)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    completion(null)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {}
            })
            .dispatch()
    }

    private fun bitmapToFile(image: Bitmap, context: Context): File {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        file.outputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, 90, it)
            it.flush()
        }
        return file
    }
}
