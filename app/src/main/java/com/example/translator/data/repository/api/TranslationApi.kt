package com.example.translator.data.repository.api

import com.example.translator.data.model.TranslateImageRequest
import com.example.translator.data.model.TranslateImageResponse
import com.example.translator.data.model.TranslateTextRequest
import com.example.translator.data.model.TranslateTextResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TranslationApi {
    @POST("/translate/text")
    fun translateText(@Body body: TranslateTextRequest): Call<TranslateTextResponse>

    @Multipart
    @POST("/translate/image")
    fun translateImage(
        @Part("body") body: TranslateImageRequest,
        @Part image: MultipartBody.Part,
    ): Call<TranslateImageResponse>
}