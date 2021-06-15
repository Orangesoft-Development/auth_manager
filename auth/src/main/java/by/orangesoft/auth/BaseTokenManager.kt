package by.orangesoft.auth

import android.util.Log
import by.orangesoft.auth.user.ITokenController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception
import java.net.HttpURLConnection
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.Throws

abstract class BaseTokenManager<T: ITokenController> (
        protected val controller: StateFlow<T>,
        protected open val authHeader: String = DEFAULT_AUTH_HEADER,
        protected open val tokenPrefix: String = DEFAULT_TOKEN_PREFIX
) : Interceptor, CoroutineScope {

    companion object {
        const val TAG = "TokenManager"
        const val DEFAULT_AUTH_HEADER = "Authorization"
        const val DEFAULT_TOKEN_PREFIX = "Bearer "
    }

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override fun intercept(chain: Interceptor.Chain): Response {
        // Trying to make request with existing access token
        var response: Response?
        runBlocking {
            val token = controller.value.getAccessToken()
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
        val headerBuilder =  request.newBuilder()
        if (authToken.isNotBlank()) {
            headerBuilder.header(authHeader, "$tokenPrefix$authToken")
        }

        return headerBuilder.build()
    }

    @Throws(Throwable::class)
    private suspend fun refreshAccessToken(successListener: () -> Unit) {
        val token = controller.value.getAccessToken()
        if (token.isNotEmpty()) {
            try {
                updateTokenApi(token)
                successListener.invoke()
            } catch (e: Exception) {
                Log.e(TAG, "Update token exception", e)
            }
        }
    }

    @Throws(Throwable::class)
    abstract suspend fun updateTokenApi(accessToken: String)
}
