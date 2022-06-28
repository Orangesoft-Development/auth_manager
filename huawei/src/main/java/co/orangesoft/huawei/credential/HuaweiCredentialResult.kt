package co.orangesoft.huawei.credential

import co.orangesoft.huawei.providers.HuaweiAuthProvider

data class HuaweiCredentialResult(
    var provider: HuaweiAuthProvider = HuaweiAuthProvider.PHONE,
    var anonymous: Boolean = false,
    var uid: String? =null,
    val email : String? = null,
    var phone: String? = null,
    var displayName: String? = null,
    var photoUrl: String? = null
    )