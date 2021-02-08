package co.orangesoft.authmanager.api

import android.util.Log
import co.orangesoft.authmanager.firebase_auth.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun provideOkHttp(interceptors: List<Interceptor> = arrayListOf(), tokenManager: TokenManager): OkHttpClient {
    val builder = OkHttpClient.Builder()
    interceptors.forEach { builder.addInterceptor(it) }

    builder.addInterceptor(tokenManager)
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
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthService::class.java)
}

internal fun provideProfileService(baseUrl: String, okHttpClient: OkHttpClient): ProfileService {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProfileService::class.java)
}

