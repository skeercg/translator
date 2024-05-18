package com.example.translator.ui.history

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translator.data.model.Favorite
import com.example.translator.data.model.Translation
import kotlinx.coroutines.launch
import com.example.translator.data.repository.TranslationRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryViewModel(
    private val translationRepository: TranslationRepository,
    private val sharedPreferences: SharedPreferences?
) : ViewModel() {

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

    fun saveToFavorite(sourceText: String, targetText: String) {
        var favoritesJsonString = sharedPreferences?.getString("favorite", null)

        val gson = Gson()

        val favorites = if (favoritesJsonString != null) {
            val type = object : TypeToken<ArrayList<Favorite>>() {}.type

            gson.fromJson(favoritesJsonString, type)
        } else {
            ArrayList<Favorite>()
        }

        favorites.add(Favorite(sourceText, targetText))

        val editor = sharedPreferences?.edit()

        favoritesJsonString = gson.toJson(favorites)

        editor?.putString("favorite", favoritesJsonString)
        editor?.apply()
    }
}
