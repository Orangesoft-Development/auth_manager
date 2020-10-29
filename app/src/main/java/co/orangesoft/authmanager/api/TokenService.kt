package co.orangesoft.authmanager.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface TokenService {
    @POST("/auth/refresh-token")
    suspend fun updateTokens(@Query("access_token") accessToken: String): Response<Unit>
}