package co.orangesoft.authmanager.api.request

data class UpdateProfileRequest(
    val name: String? = null,
    val birthday: String? = null
)