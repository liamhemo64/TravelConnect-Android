package com.idz.travelconnect.data.repository.user

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.idz.travelconnect.base.Completion
import com.idz.travelconnect.dao.AppLocalDB
import com.idz.travelconnect.data.model.FirebaseModel
import com.idz.travelconnect.data.model.StorageModel
import com.idz.travelconnect.model.User
import java.util.concurrent.Executors

class UserRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val storageModel = StorageModel()
    private val database = AppLocalDB.db
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    companion object {
        val shared = UserRepository()
    }

    fun getUser(uid: String): LiveData<User?> = database.userDao.getUserById(uid)

    fun refreshUser(uid: String, completion: Completion = {}) {
        firebaseModel.getUserById(uid) { user ->
            if (user != null) {
                executor.execute {
                    database.userDao.insertUser(user)
                    mainHandler.post { completion() }
                }
            } else {
                completion()
            }
        }
    }

    fun createUser(user: User, completion: Completion) {
        firebaseModel.saveUser(user) {
            executor.execute {
                database.userDao.insertUser(user)
                mainHandler.post { completion() }
            }
        }
    }

    fun updateProfile(
        uid: String,
        displayName: String,
        email: String,
        currentAvatarUrl: String?,
        newAvatarBitmap: Bitmap?,
        completion: Completion
    ) {
        fun saveUser(avatarUrl: String?) {
            val user = User(
                uid = uid,
                displayName = displayName,
                email = email,
                avatarUrl = avatarUrl ?: currentAvatarUrl,
                lastUpdated = System.currentTimeMillis()
            )
            firebaseModel.saveUser(user) {
                executor.execute {
                    database.userDao.insertUser(user)
                    mainHandler.post { completion() }
                }
            }
        }

        if (newAvatarBitmap != null) {
            storageModel.uploadImage(
                folderPath = "users/$uid",
                image = newAvatarBitmap
            ) { url -> saveUser(url) }
        } else {
            saveUser(null)
        }
    }
}
