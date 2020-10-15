package co.orangesoft.authmanager

import by.orangesoft.auth.credentials.ApiCredentials
import by.orangesoft.auth.credentials.phone.BasePhoneCredentialController
import by.orangesoft.auth.credentials.phone.PhoneAuthMethod
import co.orangesoft.authmanager.api.AuthService

class PhoneCredentialController(val authService: AuthService, phone: PhoneAuthMethod) : BasePhoneCredentialController(phone) {
    override suspend fun getPhoneTokenFromApi(phone: ApiCredentials.Phone): String {
        return authService.createPhoneToken(phone).custom_token
    }
}