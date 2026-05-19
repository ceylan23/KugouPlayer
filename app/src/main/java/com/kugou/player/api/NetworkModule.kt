package com.kugou.player.api

import android.content.Context
import android.content.SharedPreferences
import com.kugou.player.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

class CookieManager(private val prefs: SharedPreferences) {

    companion object {
        const val PREFS_NAME = "kugou_cookies"
        const val KEY_TOKEN = "token"
        const val KEY_USERID = "userid"
        const val KEY_GUID = "device_guid"
        const val KEY_MID = "device_mid"
    }

    fun getToken(): String = prefs.getString(KEY_TOKEN, "") ?: ""
    fun getUserId(): String = prefs.getString(KEY_USERID, "") ?: ""

    fun saveLoginState(token: String, userid: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USERID, userid)
            .apply()
    }

    fun clearLoginState() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USERID)
            .apply()
    }

    fun isLoggedIn(): Boolean = getToken().isNotEmpty() && getUserId().isNotEmpty()

    fun getOrCreateGuid(): String {
        var guid = prefs.getString(KEY_GUID, null)
        if (guid == null) {
            guid = UUID.randomUUID().toString().replace("-", "")
            prefs.edit().putString(KEY_GUID, guid).apply()
        }
        return guid
    }

    fun getOrCreateMid(): String {
        var mid = prefs.getString(KEY_MID, null)
        if (mid == null) {
            mid = UUID.randomUUID().toString().replace("-", "").substring(0, 16)
            prefs.edit().putString(KEY_MID, mid).apply()
        }
        return mid
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCookieManager(@ApplicationContext context: Context): CookieManager {
        return CookieManager(context.getSharedPreferences(CookieManager.PREFS_NAME, Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    fun provideSettingsPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cookieManager: CookieManager, settingsPrefs: SharedPreferences): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val cookieInterceptor = Interceptor { chain ->
            val original = chain.request()

            // Get the base URL from settings
            val baseUrl = settingsPrefs.getString(
                Constants.KEY_API_BASE_URL,
                Constants.DEFAULT_API_BASE_URL
            ) ?: Constants.DEFAULT_API_BASE_URL

            // Rewrite the URL to use the configured base URL
            val newUrl = original.url.newBuilder()
                .scheme("http")
                .host(baseUrl.removePrefix("http://").removePrefix("https://").split(":").first())
                .port(
                    try {
                        baseUrl.removePrefix("http://").removePrefix("https://")
                            .split(":").getOrNull(1)?.split("/")?.first()?.toInt() ?: 3000
                    } catch (_: Exception) { 3000 }
                )
                .build()

            val requestBuilder = original.newBuilder().url(newUrl)

            val guid = cookieManager.getOrCreateGuid()
            val mid = cookieManager.getOrCreateMid()
            requestBuilder.addHeader("Cookie", "KUGOU_API_GUID=$guid; KUGOU_API_MID=$mid; KUGOU_API_DEV=android")

            val token = cookieManager.getToken()
            val userid = cookieManager.getUserId()
            if (token.isNotEmpty() && userid.isNotEmpty()) {
                requestBuilder.addHeader("Cookie", "token=$token; userid=$userid")
            }

            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(cookieInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Base URL is overridden by the interceptor, but we need a valid URL here
        return Retrofit.Builder()
            .baseUrl("http://localhost:3000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
