package com.example.translator.ui.history

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.translator.R
import com.example.translator.data.model.Translation
import com.example.translator.data.repository.TranslationRepository
import com.example.translator.data.repository.api.RetrofitClient
import com.example.translator.databinding.FragmentHistoryBinding
import com.example.translator.ui.history.recycler.HistoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory(
            TranslationRepository(api = RetrofitClient.translationApi),
            activity?.getSharedPreferences(getString(R.string.favorite), Context.MODE_PRIVATE)
        )
    }
    private var adapter: HistoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistoryAdapter { sourceText, targetText ->
            viewModel.saveToFavorite(
                sourceText,
                targetText
            )
        }
        with(binding) {

            recyclerViewHistory.adapter = adapter

            recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())

            returnButton.setOnClickListener {
                handleReturnButtonClick()
            }
        }

        // Observe history list LiveData
        viewModel.historyList.observe(viewLifecycleOwner) { translations ->
            adapter?.submitList(translations)
        }

        // Fetch translations list
        fetchTranslationsList()
    }


    private fun fetchTranslationsList() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.fetchTranslationsList()
        }
    }

    private fun handleReturnButtonClick() {
        // Navigate back to the previous fragment
        requireActivity().supportFragmentManager.popBackStack()
    }
}
