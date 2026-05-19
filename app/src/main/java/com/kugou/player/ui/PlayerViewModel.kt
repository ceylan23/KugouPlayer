package com.kugou.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kugou.player.data.MusicRepository
import com.kugou.player.model.Song
import com.kugou.player.playback.PlaybackManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playbackManager: PlaybackManager,
    private val repository: MusicRepository
) : ViewModel() {

    val currentSong: StateFlow<Song?> = playbackManager.currentSong
    val isPlaying: StateFlow<Boolean> = playbackManager.isPlaying
    val position: StateFlow<Long> = playbackManager.currentPosition
    val duration: StateFlow<Long> = playbackManager.duration

    private val _lyrics = MutableStateFlow<String>("")
    val lyrics: StateFlow<String> = _lyrics.asStateFlow()

    private val _lyricsLoading = MutableStateFlow(false)
    val lyricsLoading: StateFlow<Boolean> = _lyricsLoading.asStateFlow()

    private val _shuffleMode = MutableStateFlow(false)
    val shuffleMode: StateFlow<Boolean> = _shuffleMode.asStateFlow()

    // 0 = off, 1 = repeat all, 2 = repeat one
    private val _repeatMode = MutableStateFlow(0)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    fun playSong(song: Song) {
        playbackManager.play(song)
    }

    fun togglePlayPause() {
        playbackManager.togglePlayPause()
    }

    fun skipNext() {
        playbackManager.skipNext()
    }

    fun skipPrevious() {
        playbackManager.skipPrevious()
    }

    fun seekTo(positionMs: Long) {
        playbackManager.seekTo(positionMs)
    }

    fun toggleShuffle() {
        _shuffleMode.value = !_shuffleMode.value
    }

    fun toggleRepeatMode() {
        _repeatMode.value = (_repeatMode.value + 1) % 3
    }

    fun loadLyrics(songId: String) {
        viewModelScope.launch {
            _lyricsLoading.value = true
            try {
                val lyricsText = repository.getLyrics(songId)
                _lyrics.value = lyricsText
            } catch (e: Exception) {
                _lyrics.value = "暂无歌词"
            } finally {
                _lyricsLoading.value = false
            }
        }
    }
}
