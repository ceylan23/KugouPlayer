package com.kugou.player.api

import com.google.gson.annotations.SerializedName

data class SongInfo(
    @SerializedName("SongName") val songName: String = "",
    @SerializedName("FileHash") val fileHash: String = "",
    @SerializedName("AlbumID") val albumID: String = "",
    @SerializedName("SingerName") val singerName: String = "",
    @SerializedName("SingerId") val singerId: String = "",
    @SerializedName("AlbumName") val albumName: String = "",
    @SerializedName("Duration") val duration: Long = 0,
    @SerializedName("FileName") val fileName: String = "",
    @SerializedName("MvHash") val mvHash: String = "",
    @SerializedName("Image") val image: String = "",
    @SerializedName("Lyrics") val lyrics: String = ""
)

data class SearchResult(
    @SerializedName("songs") val songs: List<SongInfo> = emptyList(),
    @SerializedName("total") val total: Int = 0
)

data class SongUrlResult(
    @SerializedName("url") val url: List<String> = emptyList(),
    @SerializedName("bitrate") val bitrate: Int = 0,
    @SerializedName("fileSize") val fileSize: Long = 0,
    @SerializedName("fileName") val fileName: String = "",
    @SerializedName("hash") val hash: String = ""
)

data class LyricResult(
    @SerializedName("content") val content: String = "",
    @SerializedName("info") val info: String = ""
)

data class RankInfo(
    @SerializedName("rankid") val rankid: String = "",
    @SerializedName("rankname") val rankname: String = "",
    @SerializedName("ranktype") val ranktype: String = "",
    @SerializedName("intro") val intro: String = "",
    @SerializedName("img") val img: String = ""
)

data class RankListResult(
    @SerializedName("list") val list: List<RankInfo> = emptyList()
)

data class RankSongResult(
    @SerializedName("songs") val songs: List<SongInfo> = emptyList(),
    @SerializedName("total") val total: Int = 0
)

data class PlaylistInfo(
    @SerializedName("global_collection_id") val globalCollectionId: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("pic") val pic: String = "",
    @SerializedName("count") val count: Int = 0,
    @SerializedName("introduction") val introduction: String = "",
    @SerializedName("nickname") val nickname: String = "",
    @SerializedName("songs") val songs: List<SongInfo> = emptyList()
)

data class PlaylistTag(
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("category_name") val categoryName: String = ""
)

data class PlaylistTrackResult(
    @SerializedName("songs") val songs: List<SongInfo> = emptyList(),
    @SerializedName("total") val total: Int = 0
)

data class ArtistInfo(
    @SerializedName("author_id") val authorId: String = "",
    @SerializedName("author_name") val authorName: String = "",
    @SerializedName("avatar") val avatar: String = "",
    @SerializedName("intro") val intro: String = ""
)

data class ArtistDetail(
    @SerializedName("info") val info: ArtistInfo = ArtistInfo(),
    @SerializedName("songs") val songs: List<SongInfo> = emptyList()
)

data class AlbumInfo(
    @SerializedName("albumid") val albumid: String = "",
    @SerializedName("albumname") val albumname: String = "",
    @SerializedName("img") val img: String = "",
    @SerializedName("intro") val intro: String = "",
    @SerializedName("publish_date") val publishDate: String = "",
    @SerializedName("singername") val singername: String = "",
    @SerializedName("songs") val songs: List<SongInfo> = emptyList()
)

data class CommentInfo(
    @SerializedName("user_name") val userName: String = "",
    @SerializedName("user_pic") val userPic: String = "",
    @SerializedName("content") val content: String = "",
    @SerializedName("like_count") val likeCount: Int = 0,
    @SerializedName("addtime") val addtime: String = ""
)

data class CommentResult(
    @SerializedName("list") val list: List<CommentInfo> = emptyList(),
    @SerializedName("total") val total: Int = 0
)

data class BannerInfo(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("img") val img: String = "",
    @SerializedName("url") val url: String = ""
)

data class FmClassInfo(
    @SerializedName("classid") val classid: String = "",
    @SerializedName("classname") val classname: String = ""
)

data class FmSongResult(
    @SerializedName("songs") val songs: List<SongInfo> = emptyList()
)

data class LoginResult(
    @SerializedName("token") val token: String = "",
    @SerializedName("userid") val userid: String = ""
)

data class QrKeyResult(
    @SerializedName("key") val key: String = ""
)

data class QrCreateResult(
    @SerializedName("qrcode") val qrcode: String = ""
)

data class QrCheckResult(
    @SerializedName("status") val status: Int = 0,
    @SerializedName("token") val token: String = "",
    @SerializedName("userid") val userid: String = ""
)

data class HotSearchItem(
    @SerializedName("keyword") val keyword: String = "",
    @SerializedName("is_hot") val isHot: Boolean = false
)

data class SuggestResult(
    @SerializedName("songs") val songs: List<SongInfo> = emptyList(),
    @SerializedName("albums") val albums: List<AlbumInfo> = emptyList(),
    @SerializedName("singers") val singers: List<ArtistInfo> = emptyList()
)

data class RecommendSongResult(
    @SerializedName("songs") val songs: List<SongInfo> = emptyList()
)

data class ImageResult(
    @SerializedName("url") val url: List<String> = emptyList()
)

data class BannerResult(
    @SerializedName("banners") val banners: List<BannerInfo> = emptyList()
)

data class NewSongResult(
    @SerializedName("songs") val songs: List<SongInfo> = emptyList()
)
