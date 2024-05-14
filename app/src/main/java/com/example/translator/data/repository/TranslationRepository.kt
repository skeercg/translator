package com.example.translator.data.repository

import com.example.translator.data.model.TranslateImageResponse
import com.example.translator.data.model.TranslateTextRequest
import com.example.translator.data.model.TranslateTextResponse
import com.example.translator.data.repository.api.TranslationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.await

class TranslationRepository(private val api: TranslationApi) {
    suspend fun translateText(request: TranslateTextRequest): TranslateTextResponse {
        return withContext(Dispatchers.IO) {
            try {
                return@withContext api.translateText(request).await<TranslateTextResponse>()
            } catch (e: Throwable) {
                throw e
            }
        }
    }

    suspend fun translateImage(): TranslateImageResponse {
        return withContext(Dispatchers.IO) {
            try {
//              return@withContext api.translateImage().await<TranslateImageResponse>()
                return@withContext TranslateImageResponse("I want to go home")
            } catch (e: Throwable) {
                throw e
            }
        }
    }
}