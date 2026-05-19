package com.kugou.player.model

import com.kugou.player.api.PlaylistInfo

data class Playlist(
    val id: String,
    val name: String,
    val coverUrl: String,
    val description: String,
    val creatorName: String,
    val songCount: Int
)

fun PlaylistInfo.toPlaylist(): Playlist = Playlist(
    id = specialid,
    name = resolvedName,
    coverUrl = resolvedCover,
    description = intro,
    creatorName = nickname.ifEmpty { singername },
    songCount = songcount
)
