package co.orangesoft.authmanager.api.responses

import android.os.Bundle
import co.orangesoft.authmanager.firebase_auth.user.Profile

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val profile: Profile
) {
    fun toBundle(firebaseUid: String? = null): Bundle =
        Bundle().apply {
            putString("firebaseUid", profile.uid)
            putString("id", profile.id)
            putString("avatarUrl", profile.avatarUrl)
            putString("birthday", profile.birthday)
            putString("country", profile.country)
            putString("city", profile.city)
        }
}