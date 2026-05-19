package com.kugou.player.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackReceiver : BroadcastReceiver() {

    @Inject
    lateinit var playbackManager: PlaybackManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.kugou.player.ACTION_PLAY" -> playbackManager.player.play()
            "com.kugou.player.ACTION_PAUSE" -> playbackManager.player.pause()
            "com.kugou.player.ACTION_NEXT" -> playbackManager.skipNext()
            "com.kugou.player.ACTION_PREVIOUS" -> playbackManager.skipPrevious()
        }
    }
}
