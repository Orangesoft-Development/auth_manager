package co.orangesoft.huawei.providers.phone

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.providers.interfaces.HuaweiAuth
import com.huawei.agconnect.auth.AGConnectUser

internal class HuaweiPhoneCredentialsController: IHuaweiAuthPhone {

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

    override fun getCurrentUser(): AGConnectUser {
        TODO("Not yet implemented")
    }

    override fun changeEmail(newEmail: String) {
        TODO("Not yet implemented")
    }

    override fun changePhone(newPhone: String) {
        TODO("Not yet implemented")
    }

    override fun changePassword(newPassword: String) {
        TODO("Not yet implemented")
    }

    override fun resetPassword(credential: HuaweiAuthCredential) {
        TODO("Not yet implemented")
    }
}