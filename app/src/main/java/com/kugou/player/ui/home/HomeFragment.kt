package com.kugou.player.ui.home

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.kugou.player.R
import com.kugou.player.databinding.FragmentHomeBinding
import com.kugou.player.model.UiState
import com.kugou.player.ui.PlayerViewModel
import com.kugou.player.ui.adapter.PlaylistAdapter
import com.kugou.player.ui.adapter.RankAdapter
import com.kugou.player.ui.adapter.SongAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupViewPager()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {
                    findNavController().navigate(R.id.searchFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupViewPager() {
        val adapter = HomePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "推荐"
                1 -> "排行榜"
                2 -> "歌单"
                else -> ""
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> RecommendFragment()
            1 -> RankFragment()
            2 -> PlaylistsFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}

@AndroidEntryPoint
class RecommendFragment : Fragment() {

    private var _binding: com.kugou.player.databinding.FragmentRecommendBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })
    private val playerViewModel: PlayerViewModel by activityViewModels()

    private lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.kugou.player.databinding.FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadRecommendSongs()
            viewModel.loadBanners()
        }
    }

    private fun setupRecyclerView() {
        songAdapter = SongAdapter { song ->
            playerViewModel.playSong(song)
        }
        binding.rvRecommendSongs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songAdapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.recommendSongs.collect { state ->
                        binding.swipeRefresh.isRefreshing = false
                        when (state) {
                            is UiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.tvEmpty.visibility = View.GONE
                            }
                            is UiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.tvEmpty.visibility = View.GONE
                                songAdapter.submitList(state.data)
                            }
                            is UiState.Empty -> {
                                binding.progressBar.visibility = View.GONE
                                binding.tvEmpty.visibility = View.VISIBLE
                            }
                            is UiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.tvEmpty.text = state.message
                                binding.tvEmpty.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                launch {
                    viewModel.banners.collect { state ->
                        if (state is UiState.Success) {
                            val bannerText = state.data.joinToString(" | ") { it.title }
                            binding.tvBannerHeader.text = bannerText
                            binding.tvBannerHeader.visibility = View.VISIBLE
                        } else {
                            binding.tvBannerHeader.visibility = View.GONE
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

@AndroidEntryPoint
class RankFragment : Fragment() {

    private var _binding: com.kugou.player.databinding.FragmentRankBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })
    private lateinit var rankAdapter: RankAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.kugou.player.databinding.FragmentRankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadRankList() }
    }

    private fun setupRecyclerView() {
        rankAdapter = RankAdapter { rank ->
            val bundle = Bundle().apply { putString("id", rank.id) }
            findNavController().navigate(R.id.rankDetailFragment, bundle)
        }
        binding.rvRankList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rankAdapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rankList.collect { state ->
                    binding.swipeRefresh.isRefreshing = false
                    when (state) {
                        is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            rankAdapter.submitList(state.data)
                        }
                        is UiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmpty.visibility = View.VISIBLE
                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmpty.text = state.message
                            binding.tvEmpty.visibility = View.VISIBLE
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

@AndroidEntryPoint
class PlaylistsFragment : Fragment() {

    private var _binding: com.kugou.player.databinding.FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.kugou.player.databinding.FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        binding.swipeRefresh.setOnRefreshListener { viewModel.loadPlaylists() }
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter { playlist ->
            val bundle = Bundle().apply { putString("id", playlist.id) }
            findNavController().navigate(R.id.playlistDetailFragment, bundle)
        }
        binding.rvPlaylists.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = playlistAdapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlists.collect { state ->
                    binding.swipeRefresh.isRefreshing = false
                    when (state) {
                        is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            playlistAdapter.submitList(state.data)
                        }
                        is UiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmpty.visibility = View.VISIBLE
                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.tvEmpty.text = state.message
                            binding.tvEmpty.visibility = View.VISIBLE
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
