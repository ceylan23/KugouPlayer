package com.kugou.player.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kugou.player.data.MusicRepository
import com.kugou.player.model.Album
import com.kugou.player.model.Artist
import com.kugou.player.model.Comment
import com.kugou.player.model.Playlist
import com.kugou.player.model.Song
import com.kugou.player.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _songs = MutableStateFlow<UiState<List<Song>>>(UiState.Loading)
    val songs: StateFlow<UiState<List<Song>>> = _songs.asStateFlow()

    private val _detail = MutableStateFlow<UiState<Any>>(UiState.Loading)
    val detail: StateFlow<UiState<Any>> = _detail.asStateFlow()

    private val _artistAlbums = MutableStateFlow<UiState<List<Album>>>(UiState.Loading)
    val artistAlbums: StateFlow<UiState<List<Album>>> = _artistAlbums.asStateFlow()

    private val _comments = MutableStateFlow<UiState<List<Comment>>>(UiState.Loading)
    val comments: StateFlow<UiState<List<Comment>>> = _comments.asStateFlow()

    fun loadPlaylistDetail(id: String) {
        viewModelScope.launch {
            _detail.value = UiState.Loading
            _songs.value = UiState.Loading
            try {
                val result = repository.getPlaylistSongs(id)
                _detail.value = UiState.Success(result.playlistId)
                _songs.value = if (result.songs.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(result.songs)
                }
            } catch (e: Exception) {
                _detail.value = UiState.Error(e.message ?: "加载歌单详情失败")
                _songs.value = UiState.Error(e.message ?: "加载歌曲失败")
            }
        }
    }

    fun loadRankDetail(id: String) {
        viewModelScope.launch {
            _detail.value = UiState.Loading
            _songs.value = UiState.Loading
            try {
                val result = repository.getRankSongs(id)
                _detail.value = UiState.Success(result.rankId)
                _songs.value = if (result.songs.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(result.songs)
                }
            } catch (e: Exception) {
                _detail.value = UiState.Error(e.message ?: "加载排行榜详情失败")
                _songs.value = UiState.Error(e.message ?: "加载歌曲失败")
            }
        }
    }

    fun loadArtistDetail(id: String) {
        viewModelScope.launch {
            _detail.value = UiState.Loading
            _songs.value = UiState.Loading
            try {
                val artist = repository.getArtistDetail(id)
                _detail.value = UiState.Success(artist)
                val artistSongs = repository.getArtistSongs(id)
                _songs.value = if (artistSongs.isEmpty()) UiState.Empty else UiState.Success(artistSongs)
            } catch (e: Exception) {
                _detail.value = UiState.Error(e.message ?: "加载歌手详情失败")
                _songs.value = UiState.Error(e.message ?: "加载歌曲失败")
            }
        }
    }

    fun loadAlbumDetail(id: String) {
        viewModelScope.launch {
            _detail.value = UiState.Loading
            _songs.value = UiState.Loading
            try {
                val album = repository.getAlbumDetail(id)
                _detail.value = UiState.Success(album)
                val albumSongs = repository.getAlbumSongs(id)
                _songs.value = if (albumSongs.isEmpty()) UiState.Empty else UiState.Success(albumSongs)
            } catch (e: Exception) {
                _detail.value = UiState.Error(e.message ?: "加载专辑详情失败")
                _songs.value = UiState.Error(e.message ?: "加载歌曲失败")
            }
        }
    }

    fun loadSongComments(songId: String) {
        viewModelScope.launch {
            _comments.value = UiState.Loading
            try {
                val commentList = repository.getSongComments(songId)
                _comments.value = if (commentList.isEmpty()) UiState.Empty else UiState.Success(commentList)
            } catch (e: Exception) {
                _comments.value = UiState.Error(e.message ?: "加载评论失败")
            }
        }
    }
}
