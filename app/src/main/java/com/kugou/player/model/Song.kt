package com.kugou.player.model

import com.kugou.player.api.SongInfo

data class Song(
    val id: String,
    val name: String,
    val artist: String,
    val artistId: String,
    val album: String,
    val albumId: String,
    val duration: Long,
    val imageUrl: String,
    val hash: String,
    var playUrl: String = ""
)

fun SongInfo.toSong(): Song = Song(
    id = hash,
    name = resolvedName,
    artist = resolvedArtist,
    artistId = singerId,
    album = albumname,
    albumId = albumId,
    duration = resolvedDuration,
    imageUrl = "",
    hash = hash
)
