package co.orangesoft.authmanager.api.response

import by.orangesoft.auth.AuthMethod
import by.orangesoft.auth.credentials.firebase.Firebase
import com.squareup.moshi.Json

data class ApiProfile(
    val id: String,
    val name: String? = null,
    @Json(name = "avatar_url")
    val avatarUrl: String? = null,
    val birthday: String? = null,
    @Json(name = "auth_credentials")
    val credentials: List<ApiProfileCredentials>
)

data class ApiProfileCredentials(
    val method: String,
    val social: String?,
    val name: String
) {
    fun toAuthMethod(): AuthMethod =
        when(method){
            "google"    -> AuthMethod("google")
            "apple"     -> AuthMethod("apple")
            "facebook"  -> AuthMethod("facebook")
            "phone"     -> AuthMethod("phone")
            "firebase"  -> {
                when(social){
                    "google"    -> Firebase.Google("")
                    "apple"     -> Firebase.Apple
                    "facebook"  -> Firebase.Facebook
                    else        -> AuthMethod("unknown")
                }
            }
            else        -> AuthMethod("unknown")
        }
}