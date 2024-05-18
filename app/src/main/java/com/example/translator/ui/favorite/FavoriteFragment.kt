package com.example.translator.ui.favorite

import android.content.Context
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
import com.example.translator.R
import com.example.translator.data.model.Translation
import com.example.translator.databinding.FragmentFavoriteBinding
import com.example.translator.ui.favorite.recycler.FavoriteAdapter
import com.example.translator.ui.favorite.viewmodel.FavoriteViewModel
import com.example.translator.ui.favorite.viewmodel.FavoriteViewModelFactory


class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: FavoriteAdapter

    private val viewModel: FavoriteViewModel by viewModels {
        FavoriteViewModelFactory(
            activity?.getSharedPreferences(getString(R.string.favorite), Context.MODE_PRIVATE)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

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

        viewModel.favoriteList.observe(viewLifecycleOwner) { translations ->
            adapter.submitList(translations.toMutableList())
        }

        viewModel.getFavorites()
    }
    private fun handleReturnButtonClick() {
        // Navigate back to the previous fragment
        requireActivity().supportFragmentManager.popBackStack()
    }
}
