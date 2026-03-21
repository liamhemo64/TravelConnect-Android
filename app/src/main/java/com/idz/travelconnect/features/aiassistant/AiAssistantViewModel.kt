package com.idz.travelconnect.features.aiassistant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.idz.travelconnect.BuildConfig
import com.idz.travelconnect.base.GEMINI_MODEL_NAME
import com.idz.travelconnect.dao.AppLocalDB
import com.idz.travelconnect.data.repository.RestCountriesRepository
import com.idz.travelconnect.data.repository.auth.AuthRepository
import com.idz.travelconnect.model.AiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AiAssistantViewModel : ViewModel() {

    private val dao = AppLocalDB.db.aiResponseDao
    private val authRepository = AuthRepository.shared

    private val currentUserId get() = authRepository.currentUser?.uid ?: ""

    val responses: LiveData<List<AiResponse>> = dao.getResponsesByUser(currentUserId)

    val isLoading = MutableLiveData(false)
    val error = MutableLiveData<String?>()

    private val generativeModel = GenerativeModel(
        modelName = GEMINI_MODEL_NAME,
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun clearError() {
        error.value = null
    }

    fun getAiResponse(country: String, userQuery: String) {
        if (country.isBlank() || userQuery.isBlank()) return

        isLoading.value = true
        error.value = null

        viewModelScope.launch {
            try {
                val countryInfoDeferred = async(Dispatchers.IO) {
                    try { RestCountriesRepository.fetchCountry(country) } catch (e: Exception) { null }
                }
                val aiDeferred = async {
                    val prompt = "You are a helpful AI travel assistant. The user is asking about $country. Answer this travel question concisely and helpfully: $userQuery. Reply in plain text only, no markdown, no bold, no bullet points, no symbols like ** or ##."
                    generativeModel.generateContent(prompt).text ?: "No response received."
                }

                val countryInfo = countryInfoDeferred.await()
                val responseText = aiDeferred.await()

                dao.insertResponse(
                    AiResponse(
                        userId = currentUserId,
                        userQuery = "$country - $userQuery",
                        aiResponse = responseText,
                        flagUrl = countryInfo?.flagUrl ?: "",
                        region = countryInfo?.region ?: "",
                        languages = countryInfo?.languages ?: "",
                        currencies = countryInfo?.currencies ?: ""
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
