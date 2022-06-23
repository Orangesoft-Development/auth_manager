package co.orangesoft.huawei

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.interfaces.IHuaweiAuthManager
import co.orangesoft.huawei.providers.HuaweiAuthProvider
import co.orangesoft.huawei.providers.email.HuaweiEmailCredentialsController
import co.orangesoft.huawei.providers.interfaces.HuaweiAuth
import co.orangesoft.huawei.providers.phone.HuaweiPhoneCredentialsController
import kotlin.properties.Delegates

object HuaweiAuthManager : IHuaweiAuthManager {

    private var huaweiAuth: HuaweiAuth by Delegates.notNull()

    fun initAuthManager(provider: HuaweiAuthProvider) {

        huaweiAuth = when (provider) {
            HuaweiAuthProvider.PHONE -> HuaweiPhoneCredentialsController.getInstance()
            HuaweiAuthProvider.EMAIL -> HuaweiEmailCredentialsController.getInstance()
        }
    }


    override fun requestSecurityCode(credential: HuaweiAuthCredential) {
        huaweiAuth.requestSecurityCode(credential)
    }

    override fun registerUser(credential: HuaweiAuthCredential) {
        huaweiAuth.registerUser(credential)
    }

    override fun signIn(credential: HuaweiAuthCredential) {
        huaweiAuth.signIn(credential)
    }

    override fun signOutUser() {
        huaweiAuth.signOutUser()
    }

    override fun deleteUser() {
        huaweiAuth.deleteUser()
    }

    override fun getCurrentUser() = huaweiAuth.getCurrentUser()

    override fun signInAnonymously() {
        huaweiAuth.signInAnonymously()
    }

    override fun resetPassword(credential: HuaweiAuthCredential) {
        huaweiAuth.resetPassword(credential)
    }
}