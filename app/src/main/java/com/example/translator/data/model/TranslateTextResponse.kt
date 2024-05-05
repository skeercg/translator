package com.example.translator.data.model

import com.google.gson.annotations.SerializedName

data class TranslateTextResponse(
    @SerializedName("translation")
    val translation: String
)
