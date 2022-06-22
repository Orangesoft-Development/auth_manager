package co.orangesoft.huawei.providers.phone

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.providers.interfaces.HuaweiAuth

internal class HuaweiPhoneCredentialsController :
    HuaweiAuth {

    companion object {

        @JvmStatic
        fun getInstance(): HuaweiAuth = HuaweiPhoneCredentialsController()

    }

    override fun requestSecurityCode(credential: HuaweiAuthCredential) {
        TODO("Not yet implemented")
    }

    override fun registerUser(credential: HuaweiAuthCredential) {
        TODO("Not yet implemented")
    }

    override fun signIn(credential: HuaweiAuthCredential) {
        TODO("Not yet implemented")
    }

    override fun signOutUser() {
        TODO("Not yet implemented")
    }

    override fun deleteUser() {
        TODO("Not yet implemented")
    }

    override fun signInAnonymously() {
        TODO("Not yet implemented")
    }

    override fun resetPassword(credential: HuaweiAuthCredential) {
        TODO("Not yet implemented")
    }


}