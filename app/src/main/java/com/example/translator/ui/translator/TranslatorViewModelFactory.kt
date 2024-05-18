package com.example.translator.ui.translator

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.translator.data.repository.TranslationRepository
import com.example.translator.data.repository.api.RetrofitClient

class TranslatorViewModelFactory(
    private val translationRepository: TranslationRepository,
    private val sharedPreferences: SharedPreferences?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TranslatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TranslatorViewModel(
                translationRepository = translationRepository,
                sharedPreferences = sharedPreferences,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}