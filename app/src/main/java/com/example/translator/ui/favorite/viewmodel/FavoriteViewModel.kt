package com.example.translator.ui.favorite.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.translator.data.model.Favorite
import com.example.translator.data.model.Translation
import com.example.translator.data.repository.TranslationRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteViewModel(
    private val sharedPreferences: SharedPreferences?
) : ViewModel() {
    private val _favoriteList = MutableLiveData<ArrayList<Favorite>>()
    val favoriteList: LiveData<ArrayList<Favorite>> get() = _favoriteList

    private val _error = MutableLiveData<String>()

    fun getFavorites() {
        val favoritesJsonString = sharedPreferences?.getString("favorite", "")

        val gson = Gson()

        val favorites = if (favoritesJsonString != null) {
            val type = object : TypeToken<ArrayList<Favorite>>() {}.type

            gson.fromJson(favoritesJsonString, type)
        } else {
            ArrayList<Favorite>()
        }
        _favoriteList.value = favorites
    }
}