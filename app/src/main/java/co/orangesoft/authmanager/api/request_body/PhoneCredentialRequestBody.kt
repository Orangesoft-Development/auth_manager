package co.orangesoft.authmanager.api.request_body

data class PhoneCredentialRequestBody(val phone: String, val code: String, val prev_user_id: String? = null)