package com.example.translator.ui.history.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.translator.data.model.Translation
import com.example.translator.databinding.HistoryItemBinding

class HistoryAdapter(private val onDeleteClickListener: (Int) ->
    Unit): ListAdapter<Translation, HistoryAdapter.ViewHolder>(HistoryDiffUtil()) {

    inner class ViewHolder(
        private val binding: HistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(translation: Translation) {

            with(binding) {
                sourceLanguageText.text = translation.sourceText
                targetLanguageText.text = translation.targetText
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HistoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    fun getTranslationAtPosition(position: Int): Translation {
        return getItem(position)
    }
    fun removeItem(position: Int) {
        val list = currentList.toMutableList()
        list.removeAt(position)
        submitList(list)
    }



}