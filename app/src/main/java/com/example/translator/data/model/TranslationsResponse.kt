package com.example.translator.data.model


data class TranslationsResponse(
    val translations: List<Translation>
)

data class Translation(
    val sourceText: String,
    val targetText: String
)

