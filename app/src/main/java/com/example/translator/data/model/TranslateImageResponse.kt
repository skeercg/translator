package com.example.translator.data.model

import com.google.gson.annotations.SerializedName

data class TranslateImageResponse(
    @SerializedName("sourceText")
    val sourceText: String,

    @SerializedName("targetText")
    val targetText: String
)