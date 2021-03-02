package co.orangesoft.authmanager.auth

import com.google.gson.annotations.SerializedName

class SimpleProfile(
    var id: String,
    var providerId: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    @SerializedName("avatar_url")
    var avatarUrl: String? = null,
    var birthday: String? = null
)