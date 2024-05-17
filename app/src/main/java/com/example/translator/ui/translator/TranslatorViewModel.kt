package com.example.translator.ui.translator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.data.model.TranslateImageRequest
import com.example.translator.data.model.TranslateTextRequest
import com.example.translator.data.model.TranslateTextResponse
import com.example.translator.data.repository.TranslationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TranslatorViewModel(
    private val translationRepository: TranslationRepository
) : ViewModel() {
    var targetLanguage: String = "rus"
    var sourceLanguage: String = "eng"

    val sourceText: MutableLiveData<String> = MutableLiveData<String>()
    val targetText: MutableLiveData<String> = MutableLiveData<String>()

    private var translationTextJob: Job? = null
    fun translateText() {
        translationTextJob?.cancel()

        translationTextJob = viewModelScope.launch {
            delay(500)

            val source = sourceText.value
            if (source == null) {
                targetText.value = ""
            } else {
                val requestText = sourceText.value ?: ""
                if (requestText.isNotEmpty()) {
                    val request = TranslateTextRequest(sourceLanguage, targetLanguage, requestText)

                    val response = translationRepository.translateText(request)

                    targetText.value = response?.translation ?: ""
                }
            }
        }
    }

    private var translationImageJob: Job? = null

    fun translateImage(image: ByteArray?) {
        translationImageJob?.cancel()

        translationImageJob = viewModelScope.launch {
            delay(500)

            if (image == null) {
                targetText.value = ""
            } else {
                val request = TranslateImageRequest(sourceLanguage, targetLanguage)

                val response = translationRepository.translateImage(request, image)

                targetText.value = response?.translation ?: ""
            }
        }
    }
}