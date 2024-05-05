package com.example.translator.data.model

import com.google.gson.annotations.SerializedName

data class TranslateTextRequest(
    @SerializedName("sourceLanguage")
    val sourceLanguage: String,
    @SerializedName("targetLanguage")
    val targetLanguage: String,
    @SerializedName("sourceText")
    val sourceText: String
)