package co.orangesoft.authmanager.api.response

import com.squareup.moshi.Json

data class ApiProfile(
    val id: String,
    val name: String? = null,
    val phoneNumber: String? = null,
    @Json(name = "avatar_url")
    val avatarUrl: String? = null,
    val birthday: String? = null)