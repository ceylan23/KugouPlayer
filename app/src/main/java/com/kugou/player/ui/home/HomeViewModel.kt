package com.kugou.player.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kugou.player.data.MusicRepository
import com.kugou.player.model.Playlist
import com.kugou.player.model.Rank
import com.kugou.player.model.Song
import com.kugou.player.model.UiState
import com.kugou.player.data.Banner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _recommendSongs = MutableStateFlow<UiState<List<Song>>>(UiState.Loading)
    val recommendSongs: StateFlow<UiState<List<Song>>> = _recommendSongs.asStateFlow()

    private val _rankList = MutableStateFlow<UiState<List<Rank>>>(UiState.Loading)
    val rankList: StateFlow<UiState<List<Rank>>> = _rankList.asStateFlow()

    private val _playlists = MutableStateFlow<UiState<List<Playlist>>>(UiState.Loading)
    val playlists: StateFlow<UiState<List<Playlist>>> = _playlists.asStateFlow()

    private val _banners = MutableStateFlow<UiState<List<Banner>>>(UiState.Loading)
    val banners: StateFlow<UiState<List<Banner>>> = _banners.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        loadBanners()
        loadRecommendSongs()
        loadRankList()
        loadPlaylists()
    }

    fun loadRecommendSongs() {
        viewModelScope.launch {
            _recommendSongs.value = UiState.Loading
            try {
                val songs = repository.getRecommendSongs()
                _recommendSongs.value = if (songs.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(songs)
                }
            } catch (e: Exception) {
                _recommendSongs.value = UiState.Error(e.message ?: "加载推荐歌曲失败")
            }
        }
    }

    fun loadRankList() {
        viewModelScope.launch {
            _rankList.value = UiState.Loading
            try {
                val ranks = repository.getRankList()
                _rankList.value = if (ranks.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(ranks)
                }
            } catch (e: Exception) {
                _rankList.value = UiState.Error(e.message ?: "加载排行榜失败")
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            _playlists.value = UiState.Loading
            try {
                val lists = repository.getPlaylists()
                _playlists.value = if (lists.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(lists)
                }
            } catch (e: Exception) {
                _playlists.value = UiState.Error(e.message ?: "加载歌单失败")
            }
        }
    }

    fun loadBanners() {
        viewModelScope.launch {
            _banners.value = UiState.Loading
            try {
                val bannerList = repository.getBanners()
                _banners.value = if (bannerList.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(bannerList)
                }
            } catch (e: Exception) {
                _banners.value = UiState.Error(e.message ?: "加载轮播图失败")
            }
        }
    }
}
