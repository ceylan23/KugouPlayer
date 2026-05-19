package com.kugou.player.util

import android.content.res.Resources
import android.widget.ImageView
import coil.load
import java.util.Locale

fun Long.formatDuration(): String {
    val totalSeconds = this
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

fun Int.toPx(): Float = this * Resources.getSystem().displayMetrics.density

fun Float.toDp(): Float = this / Resources.getSystem().displayMetrics.density

fun Int.toDp(): Float = this.toFloat().toDp()

fun String?.orDefault(default: String = ""): String = this ?: default

fun ImageView.setCoverImage(url: String?) {
    if (!url.isNullOrEmpty()) {
        this.load(url) {
            crossfade(true)
        }
    }
}
