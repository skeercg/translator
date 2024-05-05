package com.example.translator.ui.translator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.translator.data.repository.TranslationRepository
import com.example.translator.data.repository.api.RetrofitClient

class TranslatorViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TranslatorViewModel::class.java)) {
            val translationRepository = TranslationRepository(api = RetrofitClient.translationApi)
            @Suppress("UNCHECKED_CAST")
            return TranslatorViewModel(translationRepository = translationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}