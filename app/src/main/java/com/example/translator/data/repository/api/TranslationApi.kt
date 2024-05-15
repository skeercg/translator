package com.example.translator.data.repository.api

import com.example.translator.data.model.TranslateTextRequest
import com.example.translator.data.model.TranslateTextResponse
import com.example.translator.data.model.TranslationsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TranslationApi {
    @POST("/translate/text")
    fun translate(@Body body: TranslateTextRequest): Call<TranslateTextResponse>

    @GET("/translate/history")
    fun fetchTranslationsList(): Call<TranslationsResponse>


}