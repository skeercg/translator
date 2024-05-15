package com.example.translator.ui.translator

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.translator.data.model.Translation
import com.example.translator.data.repository.api.TranslationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(private val service: TranslationApi) : ViewModel() {
    class Provider(private val service: TranslationApi) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
                return HistoryViewModel(service) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val _historyList = MutableLiveData<List<Translation>>()
    val historyList: LiveData<List<Translation>> get() = _historyList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchTranslationsList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = service.fetchTranslationsList().execute()
                if (response.isSuccessful) {
                    val translationsResponse = response.body()
                    withContext(Dispatchers.Main) {
                        _historyList.value = translationsResponse?.translations ?: emptyList()
                    }
                } else {
                    _error.value = "Failed to fetch data: ${response.message()}"
                }
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Exception: ${e.message}", e)
                _historyList.postValue(emptyList())
            }
        }

    }
}
