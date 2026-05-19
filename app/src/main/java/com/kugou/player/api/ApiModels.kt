package com.kugou.player.api

import com.google.gson.annotations.SerializedName

// ========== Song Models ==========

data class SongInfo(
    @SerializedName("songname") val songname: String = "",
    @SerializedName("hash") val hash: String = "",
    @SerializedName("album_id") val albumId: String = "",
    @SerializedName("filename") val filename: String = "",
    @SerializedName("singername") val singername: String = "",
    @SerializedName("singerid") val singerId: String = "",
    @SerializedName("albumname") val albumname: String = "",
    @SerializedName("timelength") val timelength: Long = 0,
    @SerializedName("duration") val duration: Long = 0,
    @SerializedName("remark") val remark: String = "",
    @SerializedName("audio_name") val audioName: String = "",
    @SerializedName("author_name") val authorName: String = ""
) {
    val resolvedName: String get() = songname.ifEmpty { audioName }.ifEmpty { filename.substringAfter(" - ", filename) }
    val resolvedArtist: String get() {
        if (singername.isNotEmpty()) return singername
        if (authorName.isNotEmpty()) return authorName
        return filename.substringBefore(" - ", "")
    }
    val resolvedDuration: Long get() = if (timelength > 0) timelength / 1000 else duration
}

// ========== Search ==========

data class SearchListResult(
    @SerializedName("lists") val lists: List<SearchTypeList> = emptyList(),
    @SerializedName("indextotal") val indexTotal: Int = 0
)

data class SearchTypeList(
    @SerializedName("type") val type: String = "",
    @SerializedName("total") val total: Int = 0,
    @SerializedName("lists") val songs: List<SongInfo> = emptyList()
)

data class HotSearchResult(
    @SerializedName("list") val list: List<HotSearchCategory> = emptyList()
)

data class HotSearchCategory(
    @SerializedName("name") val name: String = "",
    @SerializedName("keywords") val keywords: List<HotKeyword> = emptyList()
)

data class HotKeyword(
    @SerializedName("keyword") val keyword: String = "",
    @SerializedName("reason") val reason: String = ""
)

// ========== Song URL ==========

data class SongUrlResult(
    @SerializedName("url") val url: List<String> = emptyList(),
    @SerializedName("bitrate") val bitrate: Int = 0,
    @SerializedName("fileSize") val fileSize: Long = 0,
    @SerializedName("fileName") val fileName: String = "",
    @SerializedName("hash") val hash: String = ""
)

// ========== Lyrics ==========

data class LyricResult(
    @SerializedName("content") val content: String = "",
    @SerializedName("info") val info: String = ""
)

// ========== Rank ==========

data class RankListData(
    @SerializedName("info") val info: List<RankInfo> = emptyList(),
    @SerializedName("total") val total: Int = 0
)

data class RankInfo(
    @SerializedName("rankid") val rankid: String = "",
    @SerializedName("rankname") val rankname: String = "",
    @SerializedName("ranktype") val ranktype: Int = 0,
    @SerializedName("intro") val intro: String = "",
    @SerializedName("album_img_9") val albumImg: String = "",
    @SerializedName("img_9") val img: String = "",
    @SerializedName("banner_9") val bannerImg: String = "",
    @SerializedName("update_frequency_type") val updateFrequencyType: Int = 0
)

// ========== Playlist ==========

data class PlaylistListData(
    @SerializedName("special_list") val specialList: List<PlaylistInfo> = emptyList(),
    @SerializedName("has_next") val hasNext: Int = 0
)

data class PlaylistInfo(
    @SerializedName("specialid") val specialid: String = "",
    @SerializedName("specialname") val specialname: String = "",
    @SerializedName("listname") val listname: String = "",
    @SerializedName("imgurl") val imgurl: String = "",
    @SerializedName("flexible_cover") val flexibleCover: String = "",
    @SerializedName("intro") val intro: String = "",
    @SerializedName("nickname") val nickname: String = "",
    @SerializedName("songcount") val songcount: Int = 0,
    @SerializedName("play_count") val playCount: Long = 0,
    @SerializedName("singername") val singername: String = "",
    @SerializedName("songs") val songs: List<SongInfo> = emptyList()
) {
    val resolvedName: String get() = specialname.ifEmpty { listname }
    val resolvedCover: String get() = imgurl.ifEmpty { flexibleCover }
}

data class PlaylistTrackData(
    @SerializedName("songs") val songs: List<SongInfo> = emptyList(),
    @SerializedName("info") val info: List<SongInfo> = emptyList(),
    @SerializedName("total") val total: Int = 0
)

data class PlaylistTagData(
    @SerializedName("tag_id") val tagId: String = "",
    @SerializedName("tag_name") val tagName: String = "",
    @SerializedName("parent_id") val parentId: String = "",
    @SerializedName("son") val son: List<PlaylistTagData> = emptyList()
)

// ========== Artist ==========

data class ArtistInfo(
    @SerializedName("author_id") val authorId: String = "",
    @SerializedName("author_name") val authorName: String = "",
    @SerializedName("avatar") val avatar: String = "",
    @SerializedName("intro") val intro: String = "",
    @SerializedName("sizable_avatar") val sizableAvatar: String = ""
)

data class ArtistDetailData(
    @SerializedName("author_id") val authorId: String = "",
    @SerializedName("author_name") val authorName: String = "",
    @SerializedName("avatar") val avatar: String = "",
    @SerializedName("sizable_avatar") val sizableAvatar: String = "",
    @SerializedName("intro") val intro: String = ""
)

// ========== Album ==========

data class AlbumInfo(
    @SerializedName("albumid") val albumid: String = "",
    @SerializedName("albumname") val albumname: String = "",
    @SerializedName("img") val img: String = "",
    @SerializedName("intro") val intro: String = "",
    @SerializedName("publish_date") val publishDate: String = "",
    @SerializedName("singername") val singername: String = "",
    @SerializedName("songs") val songs: List<SongInfo> = emptyList()
)

// ========== Comments ==========

data class CommentInfo(
    @SerializedName("user_name") val userName: String = "",
    @SerializedName("user_pic") val userPic: String = "",
    @SerializedName("content") val content: String = "",
    @SerializedName("like_count") val likeCount: Int = 0,
    @SerializedName("addtime") val addtime: String = ""
)

data class CommentListData(
    @SerializedName("list") val list: List<CommentInfo> = emptyList(),
    @SerializedName("total") val total: Int = 0
)

// ========== Banner ==========

data class BannerInfo(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("imgurl") val imgurl: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName("banner_9") val bannerImg: String = ""
) {
    val resolvedTitle: String get() = title.ifEmpty { name }
    val resolvedImage: String get() = imgurl.ifEmpty { bannerImg }
}

data class BannerListData(
    @SerializedName("ads") val ads: List<BannerInfo> = emptyList(),
    @SerializedName("banner") val banner: List<BannerInfo> = emptyList()
)

// ========== FM ==========

data class FmClassData(
    @SerializedName("class_list") val classList: List<FmClassGroup> = emptyList(),
    @SerializedName("update_time") val updateTime: Long = 0
)

data class FmClassGroup(
    @SerializedName("classid") val classid: Int = 0,
    @SerializedName("fmlist") val fmlist: List<FmInfo> = emptyList()
)

data class FmInfo(
    @SerializedName("fmname") val fmname: String = "",
    @SerializedName("fmid") val fmid: Int = 0,
    @SerializedName("fmtype") val fmtype: Int = 0,
    @SerializedName("songlist") val songlist: List<SongInfo> = emptyList()
)

data class FmSongData(
    @SerializedName("songs") val songs: List<SongInfo> = emptyList()
)

// ========== Recommend ==========

data class RecommendSongData(
    @SerializedName("song_list") val songList: List<SongInfo> = emptyList()
)

// ========== Login ==========

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

// ========== Images ==========

data class ImageData(
    @SerializedName("author") val author: List<ImageAuthor> = emptyList()
)

data class ImageAuthor(
    @SerializedName("author_id") val authorId: Int = 0,
    @SerializedName("author_name") val authorName: String = "",
    @SerializedName("avatar") val avatar: String = "",
    @SerializedName("sizable_avatar") val sizableAvatar: String = ""
)
