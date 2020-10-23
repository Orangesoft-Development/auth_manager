package co.orangesoft.authmanager

import android.util.Log
import androidx.lifecycle.LiveData
import co.orangesoft.authmanager.api.TokenService
import co.orangesoft.authmanager.user.UserController
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection
import kotlin.coroutines.CoroutineContext

class TokenManager(
    private val clientManager: Interceptor,
    private val user: LiveData<UserController>,
    private val tokenServiceBaseUrl: String
) : Interceptor, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    private val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(clientManager)
            .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("TokenApi", message)
                }
            }).apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()
    }


    private val tokenService: TokenService by lazy {
        Retrofit.Builder()
            .baseUrl(tokenServiceBaseUrl)
            .client(okHttp)
            .build()
            .create(TokenService::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // Trying to make request with existing access token
        var response: Response?

        runBlocking {
            val token = user.value?.getAccessToken() ?: ""
            response = chain.proceed(overrideRequest(chain.request(), token))

            // If request is failed by auth error, trying to refresh tokens and make one more request attempt
            if (response?.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                refreshAccessToken {
                    response?.close()
                    response = chain.proceed(overrideRequest(chain.request(), token))
                }
            }
        }

        return response ?: throw KotlinNullPointerException("Response is null")
    }

    private fun overrideRequest(request: Request, authToken: String): Request {
        val headerBuilder = request.newBuilder()
        if (authToken.isNotBlank())
            headerBuilder.header(AUTH, "$BEARER $authToken")

        return headerBuilder.build()
    }

    private suspend fun refreshAccessToken(successListener: () -> Unit) {
        val token = user.value?.getAccessToken() ?: ""
        if (token.isNotEmpty()) {
            val newTokensResponse = tokenService.updateTokens(token)
            if (!newTokensResponse.isSuccessful) {
                Log.e("AuthManager", newTokensResponse.message())
            } else {
                successListener.invoke()
            }
        }
    }

    companion object {
        const val TAG = "AuthManager"
        const val AUTH = "Authorization"
        const val BEARER = "Bearer"
    }
}