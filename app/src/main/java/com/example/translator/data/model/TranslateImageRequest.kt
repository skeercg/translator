package com.example.translator.data.model

import com.google.gson.annotations.SerializedName

data class TranslateImageRequest(
    @SerializedName("sourceLanguage")
    val sourceLanguage: String,

    @SerializedName("targetLanguage")
    val targetLanguage: String,
)