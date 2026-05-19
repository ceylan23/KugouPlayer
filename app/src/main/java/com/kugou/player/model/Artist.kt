package com.kugou.player.model

import com.kugou.player.api.ArtistDetailData

data class Artist(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val intro: String
)

fun ArtistDetailData.toArtist(): Artist = Artist(
    id = authorId,
    name = authorName,
    avatarUrl = avatar.ifEmpty { sizableAvatar },
    intro = intro
)
