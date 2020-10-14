package co.orangesoft.authmanager.api.request

import com.squareup.moshi.Json

data class TokenRequest(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "refresh_token")
    val refreshToken: String
)