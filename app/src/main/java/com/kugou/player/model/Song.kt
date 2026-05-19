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
    val mvHash: String,
    var playUrl: String = ""
)

fun SongInfo.toSong(): Song {
    return Song(
        id = fileHash,
        name = songName,
        artist = singerName,
        artistId = singerId,
        album = albumName,
        albumId = albumID,
        duration = duration,
        imageUrl = image,
        hash = fileHash,
        mvHash = mvHash
    )
}
