package co.orangesoft.authmanager.auth.phone_auth

data class PhoneCredentialRequestBody(val firebase_id: String?, val phone: String, val code: String)