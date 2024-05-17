package com.example.translator.ui.translator.favorite

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.translator.data.model.Translation
import com.example.translator.databinding.FragmentFavoriteBinding
import com.example.translator.ui.favorite.recycler.FavoriteAdapter
import com.example.translator.ui.favorite.viewmodel.FavoriteViewModel
import com.example.translator.ui.favorite.viewmodel.FavoriteViewModelFactory



class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: FavoriteViewModel by viewModels { FavoriteViewModelFactory() }
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FavoriteAdapter()// Initialize the adapter

        with(binding) {
            recyclerViewFavorite.adapter = adapter
            recyclerViewFavorite.layoutManager = LinearLayoutManager(requireContext())

            returnButton.setOnClickListener {
                handleReturnButtonClick()
            }
        }

        attachItemTouchHelper()

        viewModel.favoriteList.observe(viewLifecycleOwner) { translations ->
            adapter.submitList(translations)
        }

        displayFavorites()
    }

    private fun displayFavorites() {
        val sourceText = sharedPreferences.getString("sourceText", "")
        val targetText = sharedPreferences.getString("targetText", "")

        if (sourceText != null && targetText != null && sourceText.isNotEmpty() && targetText.isNotEmpty()) {
            val translation = Translation(sourceText, targetText)
            adapter.submitList(listOf(translation))
        }


    }
    private fun deleteTranslation(translation: Translation) {
        Toast.makeText(
            requireContext(),
            "Deleted from history: ${translation.sourceText}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun attachItemTouchHelper() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Not needed for swipe-to-delete functionality
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = adapter.getTranslationAtPosition(position)
                if (item != null) {
                    deleteTranslation(item)
                    adapter.removeItem(position) // Remove the item from the adapter
                    adapter.notifyItemRemoved(position) // Notify adapter of item removal
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewFavorite)
    }

    private fun handleReturnButtonClick() {
        // Navigate back to the previous fragment
        requireActivity().supportFragmentManager.popBackStack()
    }
}
