package com.kugou.player.model

import com.kugou.player.api.RankInfo

data class Rank(
    val id: String,
    val name: String,
    val type: String,
    val intro: String,
    val coverUrl: String
)

fun RankInfo.toRank(): Rank {
    return Rank(
        id = rankid,
        name = rankname,
        type = ranktype,
        intro = intro,
        coverUrl = img
    )
}
