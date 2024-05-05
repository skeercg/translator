package com.example.translator.data.repository

import com.example.translator.data.model.TranslateTextRequest
import com.example.translator.data.model.TranslateTextResponse
import com.example.translator.data.repository.api.TranslationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await

class TranslationRepository(private val api: TranslationApi) {
    suspend fun translate(request: TranslateTextRequest): TranslateTextResponse {
        return withContext(Dispatchers.IO) {
            try {
                return@withContext api.translate(request).await<TranslateTextResponse>()
            } catch (e: Throwable) {
                throw e
            }
        }
    }
}