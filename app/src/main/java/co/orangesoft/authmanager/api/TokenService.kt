package co.orangesoft.authmanager.api

import co.orangesoft.authmanager.api.request.TokenRequest
import com.squareup.moshi.Json
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface TokenService {

    @POST("/auth/refresh-token")
    suspend fun updateTokens(@Query("access_token") accessToken: String): Response<Unit>
}