package com.kugou.player.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.kugou.player.databinding.FragmentArtistDetailBinding
import com.kugou.player.model.Artist
import com.kugou.player.model.UiState
import com.kugou.player.ui.PlayerViewModel
import com.kugou.player.ui.adapter.AlbumAdapter
import com.kugou.player.ui.adapter.SongAdapter
import com.kugou.player.util.setCoverImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArtistDetailFragment : Fragment() {

    private var _binding: FragmentArtistDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    private lateinit var songAdapter: SongAdapter
    private lateinit var albumAdapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val artistId = arguments?.getString("id") ?: return
        setupToolbar()
        setupRecyclerViews()
        setupTabs()
        observeData()
        viewModel.loadArtistDetail(artistId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerViews() {
        songAdapter = SongAdapter { song ->
            playerViewModel.playSong(song)
        }
        albumAdapter = AlbumAdapter { album ->
            // Navigate to album detail
        }
        binding.contentList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }
    }

    private fun setupTabs() {
        binding.artistTabs.addTab(binding.artistTabs.newTab().setText("歌曲"))
        binding.artistTabs.addTab(binding.artistTabs.newTab().setText("专辑"))

        binding.artistTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> binding.contentList.adapter = songAdapter
                    1 -> binding.contentList.adapter = albumAdapter
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.detail.collect { state ->
                        when (state) {
                            is UiState.Success -> {
                                val artist = state.data as? Artist ?: return@collect
                                binding.collapsingToolbar.title = artist.name
                                binding.avatar.setCoverImage(artist.avatarUrl)
                                binding.intro.text = artist.intro
                            }
                            else -> {}
                        }
                    }
                }
                launch {
                    viewModel.songs.collect { state ->
                        when (state) {
                            is UiState.Success -> songAdapter.submitList(state.data)
                            else -> {}
                        }
                    }
                }
                launch {
                    viewModel.artistAlbums.collect { state ->
                        when (state) {
                            is UiState.Success -> albumAdapter.submitList(state.data)
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
