package com.idz.travelconnect.features.aiassistant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.idz.travelconnect.BuildConfig
import com.idz.travelconnect.database.AppDatabase
import com.idz.travelconnect.model.AiResponse
import kotlinx.coroutines.launch

class AiAssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).aiResponseDao()

    val responses: LiveData<List<AiResponse>> = dao.getAllResponses()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun clearError() {
        _error.value = null
    }

    fun getAiResponse(userQuery: String) {
        if (userQuery.isBlank()) return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val prompt = "You are a helpful AI travel assistant. Answer this travel question concisely and helpfully: $userQuery"
                val result = generativeModel.generateContent(prompt)
                val responseText = result.text ?: "No response received."

                dao.insertResponse(
                    AiResponse(
                        userQuery = userQuery,
                        aiResponse = responseText
                    )
                )
            } catch (e: Exception) {
                _error.value = "Failed to get AI response: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
