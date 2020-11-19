package co.orangesoft.authmanager.models

import com.google.gson.annotations.SerializedName

class Profile(
    val id: String,
    var name: String? = null,
    var phoneNumber: String? = null,
    @SerializedName("avatar_url")
    var avatarUrl: String? = null,
    var birthday: String? = null,
    var settings: Settings? = null
)

data class Settings(
    var customSetting1: String? = null,
    var customSetting2: String? = null
)