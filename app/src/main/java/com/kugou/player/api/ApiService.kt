package com.kugou.player.api

import retrofit2.http.*

interface ApiService {

    // --- Search ---

    @GET("search")
    suspend fun search(
        @Query("keywords") keywords: String,
        @Query("page") page: Int = 1,
        @Query("pagesize") pagesize: Int = 30
    ): ApiResponse<SearchResult>

    @GET("search/complex")
    suspend fun searchComplex(
        @Query("keywords") keywords: String
    ): ApiResponse<SearchResult>

    @GET("search/hot")
    suspend fun hotSearch(): ApiResponse<List<HotSearchItem>>

    @GET("search/suggest")
    suspend fun searchSuggest(
        @Query("keywords") keywords: String
    ): ApiResponse<SuggestResult>

    // --- Song ---

    @GET("song/url")
    suspend fun getSongUrl(
        @Query("hash") hash: String,
        @Query("quality") quality: Int = 320
    ): ApiResponse<SongUrlResult>

    @GET("lyric")
    suspend fun getLyric(
        @Query("id") id: String,
        @Query("accesskey") accesskey: String,
        @Query("decode") decode: Boolean = true
    ): ApiResponse<LyricResult>

    @GET("images")
    suspend fun getImages(
        @Query("hash") hash: String
    ): ApiResponse<ImageResult>

    // --- Rank ---

    @GET("rank/list")
    suspend fun getRankList(): ApiResponse<RankListResult>

    @FormUrlEncoded
    @POST("rank/audio")
    suspend fun getRankSongs(
        @Field("rankid") rankid: String
    ): ApiResponse<RankSongResult>

    // --- Playlist ---

    @GET("playlist/tags")
    suspend fun getPlaylistTags(): ApiResponse<List<PlaylistTag>>

    @GET("top/playlist")
    suspend fun browsePlaylists(
        @Query("category_id") categoryId: String = "0"
    ): ApiResponse<List<PlaylistInfo>>

    @GET("playlist/track/all")
    suspend fun getPlaylistTracks(
        @Query("id") id: String
    ): ApiResponse<PlaylistTrackResult>

    // --- Recommendations & FM ---

    @GET("recommend/songs")
    suspend fun getRecommendSongs(): ApiResponse<RecommendSongResult>

    @GET("personal/fm")
    suspend fun getPersonalFm(): ApiResponse<FmSongResult>

    @GET("fm/class")
    suspend fun getFmClasses(): ApiResponse<List<FmClassInfo>>

    @GET("fm/songs")
    suspend fun getFmSongs(
        @Query("fmid") fmid: String
    ): ApiResponse<FmSongResult>

    // --- Artist ---

    @GET("artist/detail")
    suspend fun getArtistDetail(
        @Query("id") id: String
    ): ApiResponse<ArtistDetail>

    @GET("artist/audios")
    suspend fun getArtistSongs(
        @Query("id") id: String
    ): ApiResponse<List<SongInfo>>

    // --- Album ---

    @GET("album/detail")
    suspend fun getAlbumDetail(
        @Query("id") id: String
    ): ApiResponse<AlbumInfo>

    @GET("album/songs")
    suspend fun getAlbumSongs(
        @Query("id") id: String
    ): ApiResponse<List<SongInfo>>

    // --- Comments ---

    @GET("comment/music")
    suspend fun getSongComments(
        @Query("mixsongid") mixsongid: String
    ): ApiResponse<CommentResult>

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
    suspend fun getNewSongs(): ApiResponse<NewSongResult>

    @GET("yueku/banner")
    suspend fun getBanners(): ApiResponse<BannerResult>
}
