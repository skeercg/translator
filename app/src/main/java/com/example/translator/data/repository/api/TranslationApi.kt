package com.example.translator.data.repository.api

import com.example.translator.data.model.TranslateTextRequest
import com.example.translator.data.model.TranslateTextResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TranslationApi {
    @POST("/translate/text")
    fun translate(@Body body: TranslateTextRequest): Call<TranslateTextResponse>
}