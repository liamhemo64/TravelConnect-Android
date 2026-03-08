package com.idz.travelconnect.features.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idz.travelconnect.data.repository.auth.AuthRepository

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository.shared

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _loginSuccessData = MutableLiveData<String?>()
    val loginSuccessData: LiveData<String?> get() = _loginSuccessData

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Please enter email and password."
            return
        }
        
        _isLoading.value = true
        // Entirely asynchronous callback handling wrapper
        authRepository.signIn(
            email = email.trim(),
            password = password,
            onSuccess = {
                _isLoading.value = false
                _loginSuccessData.value = email.trim() // Email as immutable param
            },
            onError = { msg ->
                _isLoading.value = false
                _error.value = msg
            }
        )
    }

    fun clearLoginSuccessData() {
        _loginSuccessData.value = null
    }
}
