package co.orangesoft.authmanager.models

import by.orangesoft.auth.user.BaseProfile
import com.google.gson.annotations.SerializedName

class Profile(
    id: String,
    var name: String? = null,
    var phoneNumber: String? = null,
    @SerializedName("avatar_url")
    var avatarUrl: String? = null,
    var birthday: String? = null,
    var settings: Settings? = null
) : BaseProfile(id)

data class Settings(
    var customSetting1: String? = null,
    var customSetting2: String? = null
)