package com.example.translator.ui.translator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.data.model.TranslateTextRequest
import com.example.translator.data.repository.TranslationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TranslatorViewModel(
    private val translationRepository: TranslationRepository
) : ViewModel() {
    var targetLanguage: String = "rus"
    var sourceLanguage: String = "eng"

    var sourceText: MutableLiveData<String> = MutableLiveData<String>()
    var targetText: MutableLiveData<String> = MutableLiveData<String>()

    private var translationJob: Job? = null
    fun translate() {
        translationJob?.cancel()

        translationJob = viewModelScope.launch {
            delay(500)

            val source = sourceText.value
            if (source == null) {
                targetText.value = ""
            } else {
                val requestText = sourceText.value ?: ""
                if (requestText.isNotEmpty()) {
                    val request = TranslateTextRequest(sourceLanguage, targetLanguage, requestText)

                    targetText.value = translationRepository.translateText(request).translation
                }
            }
        }
    }
}