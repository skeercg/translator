package com.example.translator.ui.history.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.translator.data.model.Translation

class HistoryDiffUtil: DiffUtil.ItemCallback<Translation>()  {
    override fun areItemsTheSame(oldItem: Translation, newItem: Translation): Boolean {
        return oldItem.sourceText == newItem.sourceText && oldItem.targetText==newItem.targetText
    }

    override fun areContentsTheSame(oldItem: Translation, newItem: Translation): Boolean {
        return oldItem == newItem
    }
}

