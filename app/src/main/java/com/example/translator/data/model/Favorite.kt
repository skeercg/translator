package com.example.translator.data.model

import com.google.gson.annotations.SerializedName

data class Favorite(
    @SerializedName("sourceText")
    val sourceText: String,

    @SerializedName("targetText")
    val targetText: String
)
