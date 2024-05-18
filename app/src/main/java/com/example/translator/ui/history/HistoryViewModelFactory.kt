package com.example.translator.ui.history

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.translator.data.repository.TranslationRepository
import com.example.translator.data.repository.api.RetrofitClient

class HistoryViewModelFactory(
    private val translationRepository: TranslationRepository,
    private val sharedPreferences: SharedPreferences?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(translationRepository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}