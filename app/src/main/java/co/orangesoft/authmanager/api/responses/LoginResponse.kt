package co.orangesoft.authmanager.api.responses

import android.os.Bundle
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_AVATAR_URL
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_BIRTHDAY
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_CITY
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_COUNTRY
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_FIREBASE_UID
import co.orangesoft.authmanager.firebase_auth.user.AccountManagerConst.ACCOUNT_ID
import co.orangesoft.authmanager.firebase_auth.user.Profile

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val profile: Profile
) {
    fun toBundle(firebaseUid: String? = null): Bundle =
        Bundle().apply {
            putString(ACCOUNT_ID, profile.id)
            putString(ACCOUNT_FIREBASE_UID, profile.uid)
            putString(ACCOUNT_AVATAR_URL, profile.avatarUrl)
            putString(ACCOUNT_BIRTHDAY, profile.birthday)
            putString(ACCOUNT_COUNTRY, profile.country)
            putString(ACCOUNT_CITY, profile.city)
        }
}