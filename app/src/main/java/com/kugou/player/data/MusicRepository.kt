package com.kugou.player.data

import com.kugou.player.api.ApiService
import com.kugou.player.api.CookieManager
import com.kugou.player.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val apiService: ApiService,
    private val cookieManager: CookieManager
) {

    // --- Search ---

    suspend fun searchSongs(query: String, page: Int, pageSize: Int): List<Song> {
        val response = apiService.search(query, page, pageSize)
        return response.body?.data?.songs?.map { it.toSong() } ?: emptyList()
    }

    suspend fun getHotSearches(): List<String> {
        val response = apiService.hotSearch()
        return response.body?.data?.map { it.keyword } ?: emptyList()
    }

    suspend fun getSuggestions(query: String): List<String> {
        val response = apiService.searchSuggest(query)
        return response.body?.data?.songs?.map { it.songName } ?: emptyList()
    }

    // --- Song ---

    suspend fun getSongUrl(hash: String, quality: Int = 320): String {
        val response = apiService.getSongUrl(hash, quality)
        return response.body?.data?.url?.firstOrNull() ?: ""
    }

    suspend fun getLyrics(songId: String): String {
        val response = apiService.getLyric(songId, songId)
        return response.body?.data?.content ?: ""
    }

    suspend fun getImages(hash: String): List<String> {
        val response = apiService.getImages(hash)
        return response.body?.data?.url ?: emptyList()
    }

    // --- Rank ---

    suspend fun getRankList(): List<Rank> {
        val response = apiService.getRankList()
        return response.body?.data?.list?.map { it.toRank() } ?: emptyList()
    }

    suspend fun getRankSongs(rankId: String): RankSongsResult {
        val response = apiService.getRankSongs(rankId)
        val songs = response.body?.data?.songs?.map { it.toSong() } ?: emptyList()
        return RankSongsResult(rankId, songs)
    }

    // --- Playlist ---

    suspend fun getPlaylistTags(): List<PlaylistTag> {
        val response = apiService.getPlaylistTags()
        return response.body?.data ?: emptyList()
    }

    suspend fun getPlaylists(categoryId: String = "0"): List<Playlist> {
        val response = apiService.browsePlaylists(categoryId)
        return response.body?.data?.map { it.toPlaylist() } ?: emptyList()
    }

    suspend fun getPlaylistSongs(playlistId: String): PlaylistSongsResult {
        val response = apiService.getPlaylistTracks(playlistId)
        val songs = response.body?.data?.songs?.map { it.toSong() } ?: emptyList()
        val playlistInfo = apiService.browsePlaylists("0").body?.data
            ?.find { it.globalCollectionId == playlistId }
        val playlist = playlistInfo?.toPlaylist() ?: Playlist(
            id = playlistId,
            name = "",
            coverUrl = "",
            description = "",
            creatorName = "",
            songCount = songs.size
        )
        return PlaylistSongsResult(playlist, songs)
    }

    // --- Recommendations ---

    suspend fun getRecommendSongs(): List<Song> {
        val response = apiService.getRecommendSongs()
        return response.body?.data?.songs?.map { it.toSong() } ?: emptyList()
    }

    suspend fun getPersonalFm(): List<Song> {
        val response = apiService.getPersonalFm()
        return response.body?.data?.songs?.map { it.toSong() } ?: emptyList()
    }

    // --- Artist ---

    suspend fun getArtistDetail(id: String): Artist {
        val response = apiService.getArtistDetail(id)
        return response.body?.data?.info?.toArtist() ?: Artist(id, "", "", "")
    }

    suspend fun getArtistSongs(id: String): List<Song> {
        val response = apiService.getArtistSongs(id)
        return response.body?.data?.map { it.toSong() } ?: emptyList()
    }

    // --- Album ---

    suspend fun getAlbumDetail(id: String): Album {
        val response = apiService.getAlbumDetail(id)
        val info = response.body?.data
        return info?.toAlbum() ?: Album(id, "", "", "", "", "")
    }

    suspend fun getAlbumSongs(id: String): List<Song> {
        val response = apiService.getAlbumSongs(id)
        return response.body?.data?.map { it.toSong() } ?: emptyList()
    }

    // --- Comments ---

    suspend fun getSongComments(mixSongId: String): List<Comment> {
        val response = apiService.getSongComments(mixSongId)
        return response.body?.data?.list?.map { it.toComment() } ?: emptyList()
    }

    // --- Banners ---

    suspend fun getBanners(): List<Banner> {
        val response = apiService.getBanners()
        return response.body?.data?.banners?.map {
            Banner(it.id, it.title, it.img, it.url)
        } ?: emptyList()
    }

    // --- New Songs ---

    suspend fun getNewSongs(): List<Song> {
        val response = apiService.getNewSongs()
        return response.body?.data?.songs?.map { it.toSong() } ?: emptyList()
    }

    // --- FM ---

    suspend fun getFmClasses(): List<FmClassInfo> {
        val response = apiService.getFmClasses()
        return response.body?.data ?: emptyList()
    }

    suspend fun getFmSongs(fmId: String): List<Song> {
        val response = apiService.getFmSongs(fmId)
        return response.body?.data?.songs?.map { it.toSong() } ?: emptyList()
    }

    // --- Login ---

    suspend fun loginByPhone(phone: String, code: String): LoginResult {
        val response = apiService.loginByPhone(phone, code)
        val data = response.body?.data ?: throw Exception("登录失败")
        cookieManager.saveLoginState(data.token, data.userid)
        return LoginResult(data.token, data.userid, phone, "")
    }

    suspend fun getQrKey(): String {
        val response = apiService.getQrKey()
        return response.body?.data?.key ?: throw Exception("获取二维码key失败")
    }

    suspend fun getQrCode(key: String): String {
        val response = apiService.createQrCode(key)
        return response.body?.data?.qrcode ?: throw Exception("获取二维码失败")
    }

    suspend fun checkQrStatus(key: String): QrCheckResult {
        val response = apiService.checkQrStatus(key)
        val data = response.body?.data ?: throw Exception("检查二维码状态失败")
        return QrCheckResult(data.status, data.token, data.userid, "", "")
    }

    fun isLoggedIn(): Boolean = cookieManager.isLoggedIn()

    fun saveAuth(token: String, userId: String, userName: String, avatarUrl: String) {
        cookieManager.saveLoginState(token, userId)
    }

    fun getStoredToken(): String = cookieManager.getToken()

    fun clearAuth() = cookieManager.clearLoginState()
}

data class RankSongsResult(val rankId: String, val songs: List<Song>)
data class PlaylistSongsResult(val playlist: Playlist, val songs: List<Song>)
data class Banner(val id: String, val title: String, val imageUrl: String, val url: String)
data class LoginResult(val token: String, val userId: String, val userName: String, val avatarUrl: String)
data class QrCheckResult(val statusCode: Int, val token: String, val userId: String, val userName: String, val avatarUrl: String)
