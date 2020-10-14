package co.orangesoft.authmanager.api.response

import android.os.Bundle
import co.orangesoft.authmanager.api.request.TokenRequest
import com.squareup.moshi.Json

data class LoginResponse(
    @Json(name = "auth_tokens")
    val tokens: TokenRequest,
    @Json(name = "account")
    val profile: ApiProfile
) {
    fun toBundle(firebaseUid: String? = null): Bundle =
        Bundle().apply {
            firebaseUid?.also { putString("firebaseUid", it) }
            putString("id", profile.id)
            putString("avatarUrl", profile.avatarUrl)
            putString("birthday", profile.birthday)
        }
}

data class CustomTokenResponse(val custom_token: String)