package com.idz.travelconnect.features.profile

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.idz.travelconnect.data.repository.auth.AuthRepository
import com.idz.travelconnect.data.repository.user.UserRepository
import com.idz.travelconnect.model.User

class ProfileViewModel : ViewModel() {

    private val authRepository = AuthRepository.shared
    private val userRepository = UserRepository.shared

    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()
    val profileUpdated = MutableLiveData(false)

    val currentUserId = authRepository.currentUser?.uid.toString()

    lateinit var user: LiveData<User?>

    init {
        user = userRepository.getUser(currentUserId)
        userRepository.refreshUser(currentUserId)
    }

    fun updateProfile(
        displayName: String,
        newAvatarBitmap: Bitmap?
    ) {
        if (displayName.isBlank()) {
            error.value = "Display name cannot be empty."
            return
        }
        val user = user.value ?: return

        isLoading.value = true
        val email = user.email ?: ""
        val currentAvatarUrl = user.avatarUrl

        userRepository.updateProfile(
            uid = user.uid,
            displayName = displayName.trim(),
            email = email,
            currentAvatarUrl = currentAvatarUrl,
            newAvatarBitmap = newAvatarBitmap
        ) {
            isLoading.value = false
            profileUpdated.value = true
        }
    }
}
