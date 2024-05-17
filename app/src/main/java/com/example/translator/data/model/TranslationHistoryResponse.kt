package com.example.translator.data.model


data class TranslationHistoryResponse(
    val translations: List<Translation>
)

data class Translation(
    val sourceText: String,
    val targetText: String
)

