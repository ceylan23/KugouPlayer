# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.kugou.player.api.** { *; }
-keep class com.kugou.player.model.** { *; }

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Coil
-dontwarn coil.**
-keep class coil.** { *; }

# Media3
-keep class androidx.media3.** { *; }
