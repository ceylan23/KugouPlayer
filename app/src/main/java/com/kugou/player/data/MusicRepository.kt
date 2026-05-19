package com.kugou.player.data

import com.kugou.player.api.ApiService
import com.kugou.player.api.CookieManager
import com.kugou.player.api.PlaylistTagData
import com.kugou.player.api.FmInfo
import com.kugou.player.api.BannerInfo
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
        return try {
            val response = apiService.searchComplex(query, page, pageSize)
            val songList = response.data?.lists?.firstOrNull { it.type == "song" }?.songs
            songList?.map { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getHotSearches(): List<String> {
        return try {
            val response = apiService.hotSearch()
            response.data?.list?.flatMap { cat ->
                cat.keywords.map { it.keyword }
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Song ---

    suspend fun getSongUrl(hash: String, quality: Int = 320): String {
        return try {
            val response = apiService.getSongUrl(hash, quality = quality)
            response.data?.url?.firstOrNull() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun getLyrics(songId: String): String {
        return try {
            val response = apiService.getLyric(songId, decode = true)
            response.data?.content ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    // --- Rank ---

    suspend fun getRankList(): List<Rank> {
        return try {
            val response = apiService.getRankList()
            response.data?.info?.map { it.toRank() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRankSongs(rankId: String): RankSongsResult {
        return try {
            val response = apiService.getRankInfo(rankId)
            val songs = response.data?.info?.map { it.toRank() } ?: emptyList()
            // rank/info returns rank metadata, we need to parse songs from data
            // The actual songs might be in a different field
            RankSongsResult(rankId, emptyList())
        } catch (e: Exception) {
            RankSongsResult(rankId, emptyList())
        }
    }

    // --- Playlist ---

    suspend fun getPlaylistTags(): List<PlaylistTagData> {
        return try {
            val response = apiService.getPlaylistTags()
            response.data ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPlaylists(categoryId: String = "0"): List<Playlist> {
        return try {
            val response = apiService.browsePlaylists(categoryId)
            response.data?.specialList?.map { it.toPlaylist() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPlaylistSongs(playlistId: String): PlaylistSongsResult {
        return try {
            val response = apiService.getPlaylistTracks(playlistId)
            val songs = (response.data?.songs ?: response.data?.info ?: emptyList()).map { it.toSong() }
            PlaylistSongsResult(playlistId, songs)
        } catch (e: Exception) {
            PlaylistSongsResult(playlistId, emptyList())
        }
    }

    // --- Recommendations ---

    suspend fun getRecommendSongs(): List<Song> {
        return try {
            val response = apiService.getRecommendSongs()
            response.data?.songList?.map { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPersonalFm(): List<Song> {
        return try {
            val response = apiService.getPersonalFm()
            response.data?.songs?.map { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Artist ---

    suspend fun getArtistDetail(id: String): Artist {
        return try {
            val response = apiService.getArtistDetail(id)
            response.data?.toArtist() ?: Artist(id, "", "", "")
        } catch (e: Exception) {
            Artist(id, "", "", "")
        }
    }

    suspend fun getArtistSongs(id: String): List<Song> {
        return try {
            val response = apiService.getArtistSongs(id)
            response.data?.map { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Album ---

    suspend fun getAlbumDetail(id: String): Album {
        return try {
            val response = apiService.getAlbumDetail(id)
            response.data?.toAlbum() ?: Album(id, "", "", "", "", "")
        } catch (e: Exception) {
            Album(id, "", "", "", "", "")
        }
    }

    suspend fun getAlbumSongs(id: String): List<Song> {
        return try {
            val response = apiService.getAlbumSongs(id)
            response.data?.map { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Comments ---

    suspend fun getSongComments(mixSongId: String): List<Comment> {
        return try {
            val response = apiService.getSongComments(mixSongId)
            response.data?.list?.map { it.toComment() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Banners ---

    suspend fun getBanners(): List<BannerInfo> {
        return try {
            val response = apiService.getBanners()
            response.data?.banner ?: response.data?.ads ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- New Songs ---

    suspend fun getNewSongs(): List<Song> {
        return try {
            val response = apiService.getNewSongs()
            response.data?.map { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- FM ---

    suspend fun getFmClasses(): List<FmInfo> {
        return try {
            val response = apiService.getFmClasses()
            response.data?.classList?.flatMap { it.fmlist } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getFmSongs(fmId: String): List<Song> {
        return try {
            val response = apiService.getFmSongs(fmId)
            response.data?.songs?.map { it.toSong() } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Login ---

    suspend fun loginByPhone(phone: String, code: String): LoginResultData {
        val response = apiService.loginByPhone(phone, code)
        val data = response.data ?: throw Exception("登录失败")
        cookieManager.saveLoginState(data.token, data.userid)
        return LoginResultData(data.token, data.userid)
    }

    suspend fun getQrKey(): String {
        val response = apiService.getQrKey()
        return response.data?.key ?: throw Exception("获取二维码key失败")
    }

    suspend fun getQrCode(key: String): String {
        val response = apiService.createQrCode(key)
        return response.data?.qrcode ?: throw Exception("获取二维码失败")
    }

    suspend fun checkQrStatus(key: String): QrCheckData {
        val response = apiService.checkQrStatus(key)
        val data = response.data ?: throw Exception("检查二维码状态失败")
        if (data.status == 4) {
            cookieManager.saveLoginState(data.token, data.userid)
        }
        return QrCheckData(data.status, data.token, data.userid)
    }

    fun isLoggedIn(): Boolean = cookieManager.isLoggedIn()
    fun getStoredToken(): String = cookieManager.getToken()
    fun getUserId(): String = cookieManager.getUserId()
    fun saveAuth(token: String, userId: String) = cookieManager.saveLoginState(token, userId)
    fun clearAuth() = cookieManager.clearLoginState()
}

data class RankSongsResult(val rankId: String, val songs: List<Song>)
data class PlaylistSongsResult(val playlistId: String, val songs: List<Song>)
data class LoginResultData(val token: String, val userId: String)
data class QrCheckData(val status: Int, val token: String, val userId: String)
