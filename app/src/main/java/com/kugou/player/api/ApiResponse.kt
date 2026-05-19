package com.kugou.player.api

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("status") val status: Int = 0,
    @SerializedName("body") val body: ApiBody<T>? = null,
    @SerializedName("headers") val headers: Map<String, String>? = null,
    @SerializedName("cookie") val cookie: List<String>? = null
)

data class ApiBody<T>(
    @SerializedName("status") val status: Int = 0,
    @SerializedName("error_code") val errorCode: Int = 0,
    @SerializedName("data") val data: T? = null,
    @SerializedName("error_msg") val errorMsg: String? = null
)
