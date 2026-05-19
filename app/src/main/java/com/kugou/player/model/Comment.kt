package com.kugou.player.model

import com.kugou.player.api.CommentInfo

data class Comment(
    val userName: String,
    val userAvatar: String,
    val content: String,
    val likeCount: Int,
    val addTime: String
)

fun CommentInfo.toComment(): Comment {
    return Comment(
        userName = userName,
        userAvatar = userPic,
        content = content,
        likeCount = likeCount,
        addTime = addtime
    )
}
