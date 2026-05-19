package com.kugou.player.model

import com.kugou.player.api.ArtistInfo

data class Artist(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val intro: String
)

fun ArtistInfo.toArtist(): Artist {
    return Artist(
        id = authorId,
        name = authorName,
        avatarUrl = avatar,
        intro = intro
    )
}
