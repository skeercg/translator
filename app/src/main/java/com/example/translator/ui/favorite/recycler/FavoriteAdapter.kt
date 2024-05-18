package com.example.translator.ui.favorite.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.translator.data.model.Favorite
import com.example.translator.data.model.Translation
import com.example.translator.databinding.FavoriteItemBinding

class FavoriteAdapter :
    ListAdapter<Favorite, FavoriteAdapter.ViewHolder>(FavoriteDiffUtil()) {

    inner class ViewHolder(
        private val binding: FavoriteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: Favorite) {
            with(binding) {
                binding.sourceLanguageText.text = favorite.sourceText
                binding.targetLanguageText.text = favorite.targetText
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FavoriteItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    fun getTranslationAtPosition(position: Int): Favorite {
        return getItem(position)
    }

    fun removeItem(position: Int) {
        val list = currentList.toMutableList()
        list.removeAt(position)
        submitList(list)
    }

}