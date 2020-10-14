package co.orangesoft.authmanager.api

import co.orangesoft.authmanager.api.request.TokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {

    @POST("/auth/refresh-token")
    fun updateTokens(@Body tokenPair: TokenRequest): Call<TokenRequest>
}