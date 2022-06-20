package co.orangesoft.huawei.credential

import by.orangesoft.auth.credentials.BaseAuthCredential

open class HuaweiAuthCredential(providerId: String) : BaseAuthCredential(providerId) {
    data class Phone(
        val countryCode: String = "",
        val phoneNumber: String = "",
        val securityCode: String? = null,
        val password: String? = null,
        val onCodeSentListener: ((password: String) -> Unit)? = null
    ) : HuaweiAuthCredential(Providers.PHONE)

    data class Email(
        val email: String = "",
        val securityCode: String? = null,
        val password: String? = null,
        val onCodeSentListener: ((password: String) -> Unit)? = null
    ) : HuaweiAuthCredential(Providers.EMAIL)
}

object Providers {
    const val EMAIL = "email"
    const val PHONE = "phone"

}