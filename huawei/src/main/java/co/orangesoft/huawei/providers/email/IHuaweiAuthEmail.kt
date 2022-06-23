package co.orangesoft.huawei.providers.email

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.providers.interfaces.HuaweiAuth

interface IHuaweiAuthEmail: HuaweiAuth {

    override fun requestSecurityCode(credential: HuaweiAuthCredential) {

    }
}