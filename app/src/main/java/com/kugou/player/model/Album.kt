package com.kugou.player.model

import com.kugou.player.api.AlbumInfo

data class Album(
    val id: String,
    val name: String,
    val coverUrl: String,
    val artistName: String,
    val publishDate: String,
    val intro: String
)

fun AlbumInfo.toAlbum(): Album {
    return Album(
        id = albumid,
        name = albumname,
        coverUrl = img,
        artistName = singername,
        publishDate = publishDate,
        intro = intro
    )
}
