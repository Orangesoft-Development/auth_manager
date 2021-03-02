package co.orangesoft.authmanager.api.request_body

data class EmailCredentialRequestBody(val email: String, val password: String, val prev_user_id: String? = null)