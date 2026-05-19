package com.kugou.player.api

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper matching KuGouMusicApi actual response format.
 * Most endpoints return: { status: 1, error_code: 0, data: {...} }
 * Some endpoints have errcode instead of error_code.
 */
data class ApiResponse<T>(
    @SerializedName("status") val status: Int = 0,
    @SerializedName("error_code") val errorCode: Int = 0,
    @SerializedName("errcode") val errcode: Int = 0,
    @SerializedName("error_msg") val errorMsg: String? = null,
    @SerializedName("data") val data: T? = null
) {
    val isSuccess: Boolean get() = status == 1 && (errorCode == 0 || errcode == 0)
}
