package com.kugou.player.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kugou.player.data.MusicRepository
import com.kugou.player.model.Playlist
import com.kugou.player.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserInfo(
    val userId: String,
    val userName: String,
    val avatarUrl: String
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _myPlaylists = MutableStateFlow<UiState<List<Playlist>>>(UiState.Loading)
    val myPlaylists: StateFlow<UiState<List<Playlist>>> = _myPlaylists.asStateFlow()

    private val _loginState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val loginState: StateFlow<UiState<String>> = _loginState.asStateFlow()

    private val _qrCodeUrl = MutableStateFlow<String?>(null)
    val qrCodeUrl: StateFlow<String?> = _qrCodeUrl.asStateFlow()

    private val _qrCheckStatus = MutableStateFlow<String>("")
    val qrCheckStatus: StateFlow<String> = _qrCheckStatus.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        try {
            val loggedIn = repository.isLoggedIn()
            _isLoggedIn.value = loggedIn
            if (loggedIn) {
                loadUserInfo()
            }
        } catch (_: Exception) {
            _isLoggedIn.value = false
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            try {
                val playlists = repository.getPlaylists()
                _myPlaylists.value = if (playlists.isEmpty()) UiState.Empty else UiState.Success(playlists)
            } catch (e: Exception) {
                _myPlaylists.value = UiState.Error(e.message ?: "加载失败")
            }
        }
    }

    fun loginByPhone(phone: String, code: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = repository.loginByPhone(phone, code)
                _isLoggedIn.value = true
                _userInfo.value = UserInfo(result.userId, result.userName, result.avatarUrl)
                _loginState.value = UiState.Success("登录成功")
                loadUserInfo()
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "登录失败")
            }
        }
    }

    fun loadQrCode() {
        viewModelScope.launch {
            try {
                val key = repository.getQrKey()
                val qrUrl = repository.getQrCode(key)
                _qrCodeUrl.value = qrUrl
                startQrPolling(key)
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "获取二维码失败")
            }
        }
    }

    private fun startQrPolling(key: String) {
        viewModelScope.launch {
            while (true) {
                try {
                    val status = repository.checkQrStatus(key)
                    when (status.statusCode) {
                        800 -> {
                            _qrCheckStatus.value = "二维码已过期"
                            break
                        }
                        801 -> {
                            _qrCheckStatus.value = "等待扫码..."
                        }
                        802 -> {
                            _qrCheckStatus.value = "已扫码，等待确认..."
                        }
                        803 -> {
                            repository.saveAuth(status.token, status.userId, status.userName, status.avatarUrl)
                            _isLoggedIn.value = true
                            _userInfo.value = UserInfo(status.userId, status.userName, status.avatarUrl)
                            _loginState.value = UiState.Success("登录成功")
                            _qrCheckStatus.value = ""
                            loadUserInfo()
                            break
                        }
                    }
                    kotlinx.coroutines.delay(3000)
                } catch (e: Exception) {
                    _qrCheckStatus.value = "检查状态失败"
                    break
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearAuth()
            _isLoggedIn.value = false
            _userInfo.value = null
            _myPlaylists.value = UiState.Empty
            _loginState.value = UiState.Empty
        }
    }
}
