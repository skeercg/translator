package com.example.translator.ui.history.viewmodel

import com.example.translator.ui.translator.TranslatorViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.translator.data.repository.TranslationRepository
import com.example.translator.data.repository.api.RetrofitClient
import com.example.translator.data.repository.api.TranslationApi

class HistoryViewModelFactory  : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            val translationRepository = TranslationRepository(api = RetrofitClient.translationApi)
            return HistoryViewModel(translationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}