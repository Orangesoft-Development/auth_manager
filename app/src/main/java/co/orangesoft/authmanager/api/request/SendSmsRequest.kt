package co.orangesoft.authmanager.api.request

import com.squareup.moshi.Json

data class SendSmsRequest(
    @Json(name = "phone")
    val phone: String
)