package co.orangesoft.huawei.providers.phone

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.providers.interfaces.HuaweiAuth
import com.huawei.agconnect.auth.AGConnectUser

internal class HuaweiPhoneCredentialsController :
    HuaweiAuth {

    companion object {

        @JvmStatic
        fun getInstance(): HuaweiAuth = HuaweiPhoneCredentialsController()

    }

    override fun requestSecurityCode(credential: HuaweiAuthCredential) {
        val task = agConnectAuth.requestVerifyCode(
            credential.countryCode,
            credential.phoneNumber,
            getSettings()
        )
        task.addOnSuccessListener {

        }.addOnFailureListener {

        }
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

    override fun signInAnonymously() {
        TODO("Not yet implemented")
    }

    override fun resetPassword(credential: HuaweiAuthCredential) {
        TODO("Not yet implemented")
    }


}