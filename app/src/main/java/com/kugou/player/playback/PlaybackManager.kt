package com.kugou.player.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.kugou.player.data.MusicRepository
import com.kugou.player.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: MusicRepository
) {

    val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _playQueue = MutableStateFlow<List<Song>>(emptyList())
    val playQueue: StateFlow<List<Song>> = _playQueue.asStateFlow()

    private var currentIndex = -1
    private val scope = CoroutineScope(Dispatchers.Main)
    private var positionJob: Job? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
                if (playing) startPositionUpdates() else stopPositionUpdates()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    skipNext()
                }
            }
        })
    }

    fun play(song: Song) {
        scope.launch {
            try {
                val url = if (song.playUrl.isNotEmpty()) {
                    song.playUrl
                } else {
                    repository.getSongUrl(song.hash)
                }
                if (url.isEmpty()) return@launch

                val updatedSong = song.copy(playUrl = url)
                _currentSong.value = updatedSong

                val mediaItem = MediaItem.fromUri(url)
                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
            } catch (_: Exception) {
            }
        }
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        _playQueue.value = songs
        currentIndex = startIndex
        play(songs[startIndex])
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun skipNext() {
        val queue = _playQueue.value
        if (queue.isEmpty()) return

        val nextIndex = (currentIndex + 1) % queue.size
        currentIndex = nextIndex
        play(queue[nextIndex])
    }

    fun skipPrevious() {
        val queue = _playQueue.value
        if (queue.isEmpty()) return

        if (player.currentPosition > 3000) {
            player.seekTo(0)
            return
        }

        val prevIndex = if (currentIndex - 1 < 0) queue.size - 1 else currentIndex - 1
        currentIndex = prevIndex
        play(queue[prevIndex])
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    private fun startPositionUpdates() {
        positionJob?.cancel()
        positionJob = scope.launch {
            while (true) {
                _currentPosition.value = player.currentPosition
                _duration.value = if (player.duration > 0) player.duration else 0
                delay(500)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionJob?.cancel()
    }

    fun release() {
        stopPositionUpdates()
        player.release()
    }
}
