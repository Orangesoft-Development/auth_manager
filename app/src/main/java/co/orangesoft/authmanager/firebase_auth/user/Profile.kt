package co.orangesoft.authmanager.firebase_auth.user

import com.google.gson.annotations.SerializedName

class Profile(
    id: String,
    providerId: String? = null,
    var name: String? = null,
    phoneNumber: String? = null,
    @SerializedName("avatar_url")
    var avatarUrl: String? = null,
    var birthday: String? = null,
    var settings: Settings? = null
) : by.orangesoft.auth.firebase.FirebaseProfile(uid = id, providerId = providerId, displayName = name, phoneNumber = phoneNumber, photoUrl = avatarUrl)


data class Settings(
    var customSetting1: String? = null,
    var customSetting2: String? = null
)