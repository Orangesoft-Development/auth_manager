package co.orangesoft.authmanager

import android.util.Log
import androidx.lifecycle.LiveData
import by.orangesoft.auth.BaseTokenManager
import by.orangesoft.auth.credentials.firebase.FirebaseUserController
import co.orangesoft.authmanager.api.TokenService
import co.orangesoft.authmanager.user.Profile
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class TokenManager(
    user: FirebaseUserController<Profile>,
    private val tokenServiceBaseUrl: String,
    private val interceptors: List<Interceptor>
) : BaseTokenManager<FirebaseUserController<Profile>>(user) {

    private val okHttp: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        interceptors.forEach { builder.addInterceptor(it) }

        builder.addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) { Log.d("TokenApi", message) }
        }).apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })

        return@lazy builder.build()
    }


    private val tokenService: TokenService by lazy {
        Retrofit.Builder()
            .baseUrl(tokenServiceBaseUrl)
            .client(okHttp)
            .build()
            .create(TokenService::class.java)
    }

    override suspend fun updateTokenApi(accessToken: String): ResponseModel {
        val newTokenResponse = tokenService.updateTokens(accessToken)
        return ResponseModel(newTokenResponse.isSuccessful, newTokenResponse.message())
    }
}