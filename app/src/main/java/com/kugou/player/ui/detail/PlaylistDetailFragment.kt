package com.kugou.player.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kugou.player.databinding.FragmentPlaylistDetailBinding
import com.kugou.player.model.Playlist
import com.kugou.player.model.UiState
import com.kugou.player.ui.PlayerViewModel
import com.kugou.player.ui.adapter.SongAdapter
import com.kugou.player.util.setCoverImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistDetailFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()
    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = arguments?.getString("id") ?: return
        setupToolbar()
        setupRecyclerView()
        observeData()
        viewModel.loadPlaylistDetail(playlistId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter { song ->
            playerViewModel.playSong(song)
        }
        binding.songsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.detail.collect { state ->
                        when (state) {
                            is UiState.Loading -> {}
                            is UiState.Success -> {
                                val playlist = state.data as? Playlist ?: return@collect
                                binding.collapsingToolbar.title = playlist.name
                                binding.cover.setCoverImage(playlist.coverUrl)
                                binding.description.text = playlist.description
                                binding.songCount.text = "${playlist.songCount}首歌曲"
                            }
                            is UiState.Error -> {}
                            is UiState.Empty -> {}
                        }
                    }
                }
                launch {
                    viewModel.songs.collect { state ->
                        when (state) {
                            is UiState.Success -> songAdapter.submitList(state.data)
                            is UiState.Empty -> {}
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
