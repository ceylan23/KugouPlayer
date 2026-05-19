package com.kugou.player.util

object Constants {
    const val DEFAULT_API_BASE_URL = "https://api.kugou.com/"
    const val PREF_NAME = "kugou_player_prefs"
    const val KEY_API_BASE_URL = "api_base_url"
    const val KEY_QUALITY = "audio_quality"
    const val KEY_TOKEN = "auth_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_AVATAR = "user_avatar"

    const val QUALITY_128 = 128
    const val QUALITY_320 = 320
    const val QUALITY_FLAC = 999

    val QUALITY_OPTIONS = listOf(
        QualityOption("标准音质 128kbps", QUALITY_128),
        QualityOption("高品质 320kbps", QUALITY_320),
        QualityOption("无损 FLAC", QUALITY_FLAC)
    )

    const val PAGE_SIZE = 20
    const val SEARCH_PAGE_SIZE = 20

    const val NOTIFICATION_CHANNEL_ID = "kugou_playback"
    const val NOTIFICATION_ID = 1001
}

data class QualityOption(
    val label: String,
    val value: Int
)
