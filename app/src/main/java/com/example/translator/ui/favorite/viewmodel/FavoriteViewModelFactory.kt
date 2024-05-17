package com.example.translator.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.translator.data.repository.TranslationRepository
import com.example.translator.data.repository.api.RetrofitClient

class FavoriteViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            val translationRepository = TranslationRepository(api = RetrofitClient.translationApi)
            return FavoriteViewModel(translationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}