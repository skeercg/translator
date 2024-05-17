package com.example.translator.data.repository

import com.example.translator.data.model.TranslateImageRequest
import com.example.translator.data.model.TranslateImageResponse
import com.example.translator.data.model.TranslateTextRequest
import com.example.translator.data.model.TranslateTextResponse
import com.example.translator.data.repository.api.TranslationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await


class TranslationRepository(private val api: TranslationApi) {
    suspend fun translateText(request: TranslateTextRequest): TranslateTextResponse? {
        return withContext(Dispatchers.IO) {
            try {
                return@withContext api.translateText(request).await<TranslateTextResponse>()
            } catch (e: Throwable) {
                return@withContext null
            }
        }
    }

    suspend fun translateImage(
        request: TranslateImageRequest, imageBytes: ByteArray
    ): TranslateImageResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val imageBody =
                    imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, imageBytes.size)

                val image = MultipartBody.Part.createFormData("image", null, imageBody)

                return@withContext api.translateImage(request, image)
                    .await<TranslateImageResponse>()
            } catch (e: Throwable) {
                return@withContext null
            }
        }
    }
}