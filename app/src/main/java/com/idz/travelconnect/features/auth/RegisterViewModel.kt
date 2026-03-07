package com.idz.travelconnect.features.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.travelconnect.data.repository.auth.AuthRepository

class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository.shared

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _registerSuccessData = MutableLiveData<String?>()
    val registerSuccessData: LiveData<String?> = _registerSuccessData

    fun register(displayName: String, email: String, password: String, confirmPassword: String) {
        if (displayName.isBlank() || email.isBlank() || password.isBlank()) {
            _error.value = "Please fill in all fields."
            return
        }
        if (password != confirmPassword) {
            _error.value = "Passwords do not match."
            return
        }
        if (password.length < 6) {
            _error.value = "Password must be at least 6 characters."
            return
        }

        _isLoading.value = true
        authRepository.register(
            email = email.trim(),
            password = password,
            displayName = displayName.trim(),
            onSuccess = { _ ->
                _isLoading.value = false
                _registerSuccessData.value = email.trim()
            },
            onError = { msg ->
                _isLoading.value = false
                _error.value = msg
            }
        )
    }

    fun clearRegisterSuccessData() {
        _registerSuccessData.value = null
    }
}
