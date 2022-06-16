package co.orangesoft.huawei

import by.orangesoft.auth.credentials.BaseAuthCredential

open class HuaweiAuthCredential(providerId: String) : BaseAuthCredential(providerId) {
    data class Phone(
        val phoneNumber: String = "",
        val code: String? = null,
        val password: String? = null,
        val onCodeSentListener: ((password: String) -> Unit)? = null
    ) : HuaweiAuthCredential(Providers.PHONE)

    data class Email(
        val email: String = "",
        val code: String? = null,
        val password: String? = null,
        val onCodeSentListener: ((password: String) -> Unit)? = null
    ) : HuaweiAuthCredential(Providers.EMAIL)
}

object Providers {
    const val EMAIL = "email"
    const val PHONE = "phone"

}