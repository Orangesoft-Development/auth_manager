package co.orangesoft.huawei.credential

import co.orangesoft.huawei.providers.HuaweiAuthProvider

data class HuaweiAuthCredential(
    val provider: HuaweiAuthProvider,
    val email: String = "",
    val countryCode: String = "",
    val phoneNumber: String = "",
    val securityCode: String? = null,
    val password: String? = null,
    val onCodeSentListener: ((password: String) -> Unit)? = null
)