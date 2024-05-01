package com.example.translator.data.repository.api

import retrofit2.http.GET
import retrofit2.http.POST

interface TranslationApi {
    @POST("/translate")
    suspend fun translate(): Unit
}