package com.example.translator.ui.translator

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.data.model.Favorite
import com.example.translator.data.model.TranslateImageRequest
import com.example.translator.data.model.TranslateTextRequest
import com.example.translator.data.model.TranslateTextResponse
import com.example.translator.data.repository.TranslationRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TranslatorViewModel(
    private val translationRepository: TranslationRepository,
    private val sharedPreferences: SharedPreferences?
) : ViewModel() {
    var targetLanguage: String = "rus"
    var sourceLanguage: String = "eng"

    val sourceText: MutableLiveData<String> = MutableLiveData<String>()
    val pasteSourceText: MutableLiveData<String> = MutableLiveData<String>()
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

                    targetText.value = response?.targetText ?: ""
                } else {
                    targetText.value = ""
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

                Log.d("IMAGE RESPONSE", "${response?.sourceText}: ${response?.targetText}")
                sourceText.value = response?.sourceText ?: ""
                pasteSourceText.value = response?.sourceText ?: ""
                targetText.value = response?.targetText ?: ""
            }
        }
    }

    fun saveToFavorite() {
        var favoritesJsonString = sharedPreferences?.getString("favorite", null)

        val gson = Gson()

        val favorites = if (favoritesJsonString != null) {
            val type = object : TypeToken<ArrayList<Favorite>>() {}.type

            gson.fromJson(favoritesJsonString, type)
        } else {
            ArrayList<Favorite>()
        }

        val sourceText = sourceText.value ?: ""
        val targetText = targetText.value ?: ""

        favorites.add(Favorite(sourceText, targetText))

        val editor = sharedPreferences?.edit()

        favoritesJsonString = gson.toJson(favorites)

        editor?.putString("favorite", favoritesJsonString)
        editor?.apply()
    }
}