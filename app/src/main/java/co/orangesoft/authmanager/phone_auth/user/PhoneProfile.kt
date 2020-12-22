package co.orangesoft.authmanager.phone_auth.user

import com.google.gson.annotations.SerializedName

data class PhoneProfile(val id: String,
                        val phoneNumber: String? = null,
                        var name: String? = null,
                        @SerializedName("avatar_url")
                        var avatarUrl: String? = null,
                        var birthday: String? = null)