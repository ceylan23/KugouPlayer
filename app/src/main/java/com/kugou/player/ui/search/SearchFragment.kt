package com.kugou.player.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.kugou.player.R
import com.kugou.player.databinding.FragmentSearchBinding
import com.kugou.player.model.UiState
import com.kugou.player.ui.PlayerViewModel
import com.kugou.player.ui.adapter.SongAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupSearchInput()
        setupRecyclerView()
        observeData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSearchInput() {
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.search(query)
                    hideKeyboard()
                }
                true
            } else false
        }

        binding.searchInput.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) {
                viewModel.clearResults()
                showHotSearches()
            }
        }
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter { song ->
            playerViewModel.playSong(song)
        }
        binding.searchResultsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    if (lastVisibleItem >= totalItemCount - 5) {
                        viewModel.loadMore()
                    }
                }
            })
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.hotSearches.collect { hotList ->
                        if (hotList.isNotEmpty()) {
                            showHotSearchChips(hotList)
                        }
                    }
                }
                launch {
                    viewModel.searchResults.collect { state ->
                        when (state) {
                            is UiState.Loading -> {
                                binding.emptyState.isVisible = false
                                binding.hotSearchChips.isVisible = false
                            }
                            is UiState.Success -> {
                                binding.emptyState.isVisible = false
                                binding.hotSearchChips.isVisible = false
                                songAdapter.submitList(state.data)
                            }
                            is UiState.Empty -> {
                                binding.emptyState.text = "未找到相关结果"
                                binding.emptyState.isVisible = true
                            }
                            is UiState.Error -> {
                                binding.emptyState.text = state.message
                                binding.emptyState.isVisible = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showHotSearches() {
        val hotList = viewModel.hotSearches.value
        if (hotList.isNotEmpty()) {
            showHotSearchChips(hotList)
        }
    }

    private fun showHotSearchChips(hotList: List<String>) {
        binding.hotSearchChips.removeAllViews()
        binding.hotSearchChips.isVisible = true
        for (hotWord in hotList) {
            val chip = Chip(requireContext()).apply {
                text = hotWord
                isClickable = true
                isCheckable = false
                setOnClickListener {
                    binding.searchInput.setText(hotWord)
                    viewModel.search(hotWord)
                }
            }
            binding.hotSearchChips.addView(chip)
        }
    }

    private fun hideKeyboard() {
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
