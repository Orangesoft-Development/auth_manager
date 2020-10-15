package co.orangesoft.authmanager

import android.util.Log
import androidx.lifecycle.LiveData
import co.orangesoft.authmanager.api.TokenService
import co.orangesoft.authmanager.api.request.TokenRequest
import co.orangesoft.authmanager.user.UserController
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection

class TokenManager(
    private val clientManager: Interceptor,
    private val user: LiveData<UserController>,
    private val tokenServiceBaseUrl: String
) : Interceptor {

    private val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(clientManager)
            .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) { Log.d("TokenApi", message) }
            }).apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()
    }


    private val tokenService: TokenService by lazy {
        Retrofit.Builder()
            .baseUrl(tokenServiceBaseUrl)
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
            .client(okHttp)
            .build()
            .create(TokenService::class.java)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // Trying to make request with existing access token
        val response = chain.proceed(overrideRequest(chain.request(), user.value?.getAccessToken() ?: ""))

        // If request is failed by auth error, trying to refresh tokens and make one more request attempt
        return if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            synchronized(this) {
                refreshAccessToken()?.let {
                    response.close()
                    chain.proceed(overrideRequest(chain.request(), it))
                } ?: response
            }
        } else
             response
    }

    private fun overrideRequest(request: Request, authToken: String): Request {
        val headerBuilder =  request.newBuilder()
        if(authToken.isNotBlank())
            headerBuilder.header(AUTH, "$BEARER $authToken")

        return headerBuilder.build()
    }

    private fun refreshAccessToken(): String? {
        val accessToken = user.value?.getAccessToken()?.notEmpty() ?: run {
            Log.e(TAG, "Access token not found")
            return null
        }
        val refreshToken = user.value?.getRefreshToken()?.notEmpty() ?: run {
            Log.e(TAG, "Refresh token not found")
            return null
        }
        val currentTokens = TokenRequest(accessToken, refreshToken)
        val newTokensResponse = tokenService.updateTokens(currentTokens).execute()
        if(!newTokensResponse.isSuccessful) {
            Log.e("AuthManager", newTokensResponse.errorBody()?.string())
            return null
        }

        return newTokensResponse.body()?.let { tokenPair ->
            user.value!!.updateAccessToken(tokenPair.accessToken)
            user.value!!.updateRefreshToken(tokenPair.refreshToken)
            tokenPair.accessToken
        }
    }

    private fun String.notEmpty(): String? {
        return if (isEmpty()) {
            null
        } else {
            this
        }
    }

    companion object {
        const val TAG = "AuthManager"
        const val AUTH = "Authorization"
        const val BEARER = "Bearer"
    }
}