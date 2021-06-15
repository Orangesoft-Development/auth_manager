package co.orangesoft.authmanager.firebase_auth

import android.util.Log
import by.orangesoft.auth.BaseTokenManager
import by.orangesoft.auth.user.ITokenController
import co.orangesoft.authmanager.api.TokenService
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class TokenManager(
    controller: StateFlow<ITokenController>,
    private val tokenServiceBaseUrl: String,
    private val interceptors: List<Interceptor>
) : BaseTokenManager<ITokenController>(controller) {

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

    override suspend fun updateTokenApi(accessToken: String) {
        tokenService.updateTokens(accessToken).body()?.let {
            controller.value.setAccessToken(it)
        }
    }
}