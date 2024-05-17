package com.example.translator.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.data.model.Translation
import kotlinx.coroutines.launch
import com.example.translator.data.repository.TranslationRepository

class HistoryViewModel(private val translationRepository: TranslationRepository) : ViewModel() {

    private val _historyList = MutableLiveData<List<Translation>>()
    val historyList: LiveData<List<Translation>> get() = _historyList

    private val _error = MutableLiveData<String>()
    //val error: LiveData<String> get() = _error

    fun fetchTranslationsList() {
        viewModelScope.launch {
            try {
                // Call the suspend function to get translations
                val translationsResponse = translationRepository.getTranslation()

                // Update the LiveData with the fetched translations
                _historyList.value = translationsResponse.translations
            } catch (e: Exception) {
                // Handle the error if any
                _error.value = e.message ?: "Unknown error occurred"
            }
        }
    }
}
