package com.kugou.player.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kugou.player.R
import com.kugou.player.databinding.ActivityPlayerBinding
import com.kugou.player.util.formatDuration
import com.kugou.player.util.setCoverImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupControls()
        observeState()
    }

    private fun setupControls() {
        binding.backButton.setOnClickListener { finish() }

        binding.playPause.setOnClickListener {
            viewModel.togglePlayPause()
        }

        binding.skipPrevious.setOnClickListener {
            viewModel.skipPrevious()
        }

        binding.skipNext.setOnClickListener {
            viewModel.skipNext()
        }

        binding.seekbar.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                binding.currentTime.text = value.toLong().formatDuration()
                viewModel.seekTo(value.toLong())
            }
        }

        binding.lyricsButton.setOnClickListener {
            showLyricsBottomSheet()
        }

        binding.shuffleButton.setOnClickListener {
            viewModel.toggleShuffle()
        }

        binding.repeatButton.setOnClickListener {
            viewModel.toggleRepeatMode()
        }

        binding.queueButton.setOnClickListener {
            // TODO: Show play queue
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentSong.collect { song ->
                        song?.let {
                            binding.songName.text = it.name
                            binding.artistName.text = it.artist
                            binding.albumArt.setCoverImage(it.imageUrl)
                            binding.seekbar.valueTo = it.duration.toFloat()
                            viewModel.loadLyrics(it.id)
                        }
                    }
                }
                launch {
                    viewModel.isPlaying.collect { isPlaying ->
                        binding.playPause.setImageResource(
                            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                        )
                    }
                }
                launch {
                    viewModel.position.collect { position ->
                        binding.seekbar.value = position.toFloat().coerceIn(
                            binding.seekbar.valueFrom,
                            binding.seekbar.valueTo
                        )
                        binding.currentTime.text = position.formatDuration()
                    }
                }
                launch {
                    viewModel.duration.collect { duration ->
                        binding.seekbar.valueTo = duration.toFloat()
                        binding.totalTime.text = duration.formatDuration()
                    }
                }
                launch {
                    viewModel.shuffleMode.collect { enabled ->
                        binding.shuffleButton.isSelected = enabled
                    }
                }
                launch {
                    viewModel.repeatMode.collect { mode ->
                        when (mode) {
                            0 -> binding.repeatButton.setImageResource(R.drawable.ic_repeat)
                            1 -> binding.repeatButton.setImageResource(R.drawable.ic_repeat)
                            2 -> binding.repeatButton.setImageResource(R.drawable.ic_repeat_one)
                        }
                    }
                }
            }
        }
    }

    private fun showLyricsBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val lyricsView = layoutInflater.inflate(R.layout.dialog_lyrics, null)
        val tvLyrics = lyricsView.findViewById<TextView>(R.id.lyrics_content)
        val btnClose = lyricsView.findViewById<com.google.android.material.button.MaterialButton>(R.id.close_button)

        btnClose?.setOnClickListener { dialog.dismiss() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.lyrics.collect { lyrics ->
                    tvLyrics?.text = lyrics.ifEmpty { "暂无歌词" }
                }
            }
        }

        dialog.setContentView(lyricsView)
        dialog.show()
    }
}
