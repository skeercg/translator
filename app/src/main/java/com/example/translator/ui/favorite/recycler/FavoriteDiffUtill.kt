package com.example.translator.ui.favorite.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.translator.data.model.Favorite
import com.example.translator.data.model.Translation

class FavoriteDiffUtil: DiffUtil.ItemCallback<Favorite>() {
    override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem.sourceText == newItem.sourceText && oldItem.targetText == newItem.targetText
    }
    override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem == newItem
    }
}