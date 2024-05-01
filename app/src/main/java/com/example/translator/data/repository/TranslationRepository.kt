package com.example.translator.data.repository

import com.example.translator.data.repository.api.TranslationApi

class TranslationRepository(private val api: TranslationApi) {
    suspend fun translate(source: String): String {
        return source
    }
}