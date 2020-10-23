package by.orangesoft.auth

import android.util.Log
import androidx.lifecycle.LiveData
import by.orangesoft.auth.user.BaseUserController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.net.HttpURLConnection
import kotlin.coroutines.CoroutineContext

abstract class BaseTokenManager<T : BaseUserController<*, *>> (
    private val user: LiveData<T>,
    protected open val AUTH_HEADER: String = DEFAULT_AUTH_HEADER,
    protected open val TOKEN_PREFIX: String = DEFAULT_TOKEN_PREFIX
) : Interceptor, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    override fun intercept(chain: Interceptor.Chain): Response {
        // Trying to make request with existing access token

        var response: Response? = null

        launch {
            withContext(coroutineContext) {
               user.value?.getAccessToken {
                   //TODO check this
                    response = chain.proceed(overrideRequest(chain.request(), it))

                    // If request is failed by auth error, trying to refresh tokens and make one more request attempt
                    if (response?.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        refreshAccessToken {
                            response?.close()
                            response = chain.proceed(overrideRequest(chain.request(), it))
                        }
                    }
                }
            }
        }

        return response ?: throw KotlinNullPointerException("Response is null")
    }

    private fun overrideRequest(request: Request, authToken: String): Request {
        val headerBuilder =  request.newBuilder()
        if (authToken.isNotBlank()) {
            headerBuilder.header(AUTH_HEADER, "$TOKEN_PREFIX$authToken")
        }

        return headerBuilder.build()
    }

    private suspend fun refreshAccessToken(successListener: () -> Unit) {
        user.value?.getAccessToken {
            if (it.isNotEmpty()) {
                val responseModel = updateTokenApi(it)
                if (!responseModel.isSuccessful) {
                    Log.e(TAG, responseModel.errorMessage)
                } else {
                    successListener.invoke()
                }
            }
        }
    }

    abstract suspend fun updateTokenApi(accessToken: String) : ResponseModel

    data class ResponseModel(val isSuccessful: Boolean, val errorMessage: String? = null)

    companion object {
        const val TAG = "TokenManager"
        const val DEFAULT_AUTH_HEADER = "Authorization"
        const val DEFAULT_TOKEN_PREFIX = "Bearer "
    }
}