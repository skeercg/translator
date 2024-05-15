import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.translator.data.model.Translation
import com.example.translator.data.repository.api.RetrofitClient
import com.example.translator.data.repository.api.TranslationApi
import com.example.translator.databinding.FragmentHistoryBinding
import com.example.translator.ui.translator.HistoryAdapter
import com.example.translator.ui.translator.HistoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(
            this,
            HistoryViewModel.Provider(RetrofitClient.translationApi)
        ).get<HistoryViewModel>(HistoryViewModel::class.java)
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

        adapter = HistoryAdapter { position ->
            // Handle delete button click
            val item = adapter?.getTranslationAtPosition(position)
            if (item != null) {
                // Perform delete operation based on item or position
                deleteTranslation(item)
            }
        }
        binding.recyclerViewHistory.adapter = adapter
        binding.returnButton.setOnClickListener(){
            handleReturnButtonClick()
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

    private fun deleteTranslation(translation: Translation) {
        Toast.makeText(requireContext(), "Deleted from history: ${translation.sourceText}", Toast.LENGTH_SHORT).show()
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
                val item = adapter?.getTranslationAtPosition(position)
                if (item != null) {
                    deleteTranslation(item)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewHistory)
    }

    private fun handleReturnButtonClick() {
        // Navigate back to the previous fragment
        requireActivity().supportFragmentManager.popBackStack()
    }


}
