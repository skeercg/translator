package com.example.translator.ui.favorite.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.translator.data.model.Translation
import com.example.translator.data.repository.TranslationRepository

class FavoriteViewModel(private val translationRepository: TranslationRepository) : ViewModel() {
    private val _favoriteList = MutableLiveData<List<Translation>>()
    val favoriteList: LiveData<List<Translation>> get() = _favoriteList

    private val _error = MutableLiveData<String>()


}