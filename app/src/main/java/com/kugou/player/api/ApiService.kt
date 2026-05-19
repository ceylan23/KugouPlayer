package com.kugou.player.api

import retrofit2.http.*

interface ApiService {

    // --- Search ---
    @GET("search/complex")
    suspend fun searchComplex(
        @Query("keywords") keywords: String,
        @Query("page") page: Int = 1,
        @Query("pagesize") pagesize: Int = 30
    ): ApiResponse<SearchListResult>

    @GET("search/hot")
    suspend fun hotSearch(): ApiResponse<HotSearchResult>

    @GET("search/suggest")
    suspend fun searchSuggest(
        @Query("keywords") keywords: String
    ): ApiResponse<Any>

    // --- Song URL ---
    @GET("song/url")
    suspend fun getSongUrl(
        @Query("hash") hash: String,
        @Query("album_id") albumId: String = "",
        @Query("album_audio_id") albumAudioId: String = "",
        @Query("quality") quality: Int = 320
    ): ApiResponse<SongUrlResult>

    @GET("lyric")
    suspend fun getLyric(
        @Query("id") id: String,
        @Query("accesskey") accesskey: String = "",
        @Query("decode") decode: Boolean = true
    ): ApiResponse<LyricResult>

    // --- Rank ---
    @GET("rank/list")
    suspend fun getRankList(): ApiResponse<RankListData>

    @GET("rank/info")
    suspend fun getRankInfo(
        @Query("rankid") rankid: String,
        @Query("page") page: Int = 1,
        @Query("pagesize") pagesize: Int = 30
    ): ApiResponse<RankListData>

    // --- Playlist ---
    @GET("playlist/tags")
    suspend fun getPlaylistTags(): ApiResponse<List<PlaylistTagData>>

    @GET("top/playlist")
    suspend fun browsePlaylists(
        @Query("category_id") categoryId: String = "0",
        @Query("pagesize") pagesize: Int = 20
    ): ApiResponse<PlaylistListData>

    @GET("playlist/track/all")
    suspend fun getPlaylistTracks(
        @Query("id") id: String,
        @Query("page") page: Int = 1,
        @Query("pagesize") pagesize: Int = 100
    ): ApiResponse<PlaylistTrackData>

    // --- Recommendations ---
    @GET("recommend/songs")
    suspend fun getRecommendSongs(): ApiResponse<RecommendSongData>

    @GET("personal/fm")
    suspend fun getPersonalFm(): ApiResponse<FmSongData>

    @GET("fm/class")
    suspend fun getFmClasses(): ApiResponse<FmClassData>

    @GET("fm/songs")
    suspend fun getFmSongs(
        @Query("fmid") fmid: String,
        @Query("fmsize") fmsize: Int = 20
    ): ApiResponse<FmSongData>

    // --- Artist ---
    @GET("artist/detail")
    suspend fun getArtistDetail(
        @Query("id") id: String
    ): ApiResponse<ArtistDetailData>

    @GET("artist/audios")
    suspend fun getArtistSongs(
        @Query("id") id: String,
        @Query("page") page: Int = 1,
        @Query("pagesize") pagesize: Int = 30
    ): ApiResponse<List<SongInfo>>

    // --- Album ---
    @GET("album/detail")
    suspend fun getAlbumDetail(
        @Query("id") id: String
    ): ApiResponse<AlbumInfo>

    @GET("album/songs")
    suspend fun getAlbumSongs(
        @Query("id") id: String,
        @Query("page") page: Int = 1,
        @Query("pagesize") pagesize: Int = 30
    ): ApiResponse<List<SongInfo>>

    // --- Comments ---
    @GET("comment/music")
    suspend fun getSongComments(
        @Query("mixsongid") mixsongid: String,
        @Query("page") page: Int = 1,
        @Query("pagesize") pagesize: Int = 20
    ): ApiResponse<CommentListData>

    // --- Login ---
    @FormUrlEncoded
    @POST("login/cellphone")
    suspend fun loginByPhone(
        @Field("mobile") mobile: String,
        @Field("code") code: String
    ): ApiResponse<LoginResult>

    @GET("login/qr/key")
    suspend fun getQrKey(): ApiResponse<QrKeyResult>

    @GET("login/qr/create")
    suspend fun createQrCode(
        @Query("key") key: String
    ): ApiResponse<QrCreateResult>

    @GET("login/qr/check")
    suspend fun checkQrStatus(
        @Query("key") key: String
    ): ApiResponse<QrCheckResult>

    // --- New Songs & Banners ---
    @GET("top/song")
    suspend fun getNewSongs(): ApiResponse<List<SongInfo>>

    @GET("yueku/banner")
    suspend fun getBanners(): ApiResponse<BannerListData>

    // --- Images ---
    @GET("images")
    suspend fun getImages(
        @Query("hash") hash: String
    ): ApiResponse<List<ImageData>>
}
