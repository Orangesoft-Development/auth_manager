package co.orangesoft.authmanager.api

import android.util.Log
import co.orangesoft.authmanager.firebase_auth.AuthManager
import co.orangesoft.authmanager.firebase_auth.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

fun provideOkHttp(interceptors: List<Interceptor> = arrayListOf()): OkHttpClient {
    val builder = OkHttpClient.Builder()
    interceptors.forEach { builder.addInterceptor(it) }

    builder.addInterceptor(provideTokenInterceptor(AuthManager.BASE_URL))
    builder.addInterceptor(
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("OkHttp", message)
                }
            }).apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        )
    return builder.build()
}

internal fun provideAuthService(baseUrl: String, okHttpClient: OkHttpClient): AuthService {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()
        .create(AuthService::class.java)
}

internal fun provideProfileService(baseUrl: String, okHttpClient: OkHttpClient): ProfileService {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()
        .create(ProfileService::class.java)
}

fun provideTokenInterceptor(tokenServiceBaseUrl: String,
                            interceptors: List<Interceptor> = arrayListOf()): TokenManager {
    return TokenManager(tokenServiceBaseUrl, interceptors)
}
