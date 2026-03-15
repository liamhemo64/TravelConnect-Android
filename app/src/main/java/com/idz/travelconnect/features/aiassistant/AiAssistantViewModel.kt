package com.idz.travelconnect.features.aiassistant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.idz.travelconnect.BuildConfig
import com.idz.travelconnect.base.GEMINI_MODEL_NAME
import com.idz.travelconnect.dao.AppLocalDB
import com.idz.travelconnect.model.AiResponse
import kotlinx.coroutines.launch

class AiAssistantViewModel : ViewModel() {

    private val dao = AppLocalDB.db.aiResponseDao

    val responses: LiveData<List<AiResponse>> = dao.getAllResponses()

    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()

    private val generativeModel = GenerativeModel(
        modelName = GEMINI_MODEL_NAME,
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun clearError() {
        error.value = null
    }

    fun getAiResponse(userQuery: String) {
        if (userQuery.isBlank()) return

        isLoading.value = true
        error.value = null

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
                error.value = "Failed to get AI response: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
