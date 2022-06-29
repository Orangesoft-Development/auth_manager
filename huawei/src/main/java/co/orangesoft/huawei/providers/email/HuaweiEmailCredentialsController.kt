package co.orangesoft.huawei.providers.email

import co.orangesoft.huawei.credential.HuaweiAuthCredential
import co.orangesoft.huawei.credential.HuaweiCredentialResult
import co.orangesoft.huawei.providers.HuaweiAuthProvider
import co.orangesoft.huawei.providers.base.BaseHuaweiCredentialsController
import co.orangesoft.huawei.providers.interfaces.HuaweiAuth
import com.huawei.agconnect.auth.AGConnectAuthCredential
import com.huawei.agconnect.auth.EmailAuthProvider
import com.huawei.agconnect.auth.EmailUser

class HuaweiEmailCredentialsController : BaseHuaweiCredentialsController() {

    companion object {

        @JvmStatic
        fun getInstance(): HuaweiAuth = HuaweiEmailCredentialsController()

    }

    override fun requestSecurityCode(credential: HuaweiAuthCredential) {
        val task = agConnectAuth.requestVerifyCode(credential.email, getSettings())
        task.addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    override fun registerUser(credential: HuaweiAuthCredential) {
        val emailUser = EmailUser.Builder()
            .setEmail(credential.email)
            .setVerifyCode(credential.securityCode)
            .setPassword(credential.password)
            .build()
        agConnectAuth.createUser(emailUser).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }

    override fun signIn(credential: HuaweiAuthCredential) {
        val emailCredential =
            EmailAuthProvider.credentialWithPassword(credential.email, credential.password)
        agConnectAuth.signIn(emailCredential).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }


    override fun changeEmail(newEmail: String, newVerifyCode: String) {
        agConnectAuth.currentUser.updateEmail(newEmail, newVerifyCode)
            .addOnSuccessListener {
                // onSuccess
            }.addOnFailureListener {
                // onFail
            }
    }

    override fun changePhone(newCountryCode: String, newPhone: String, newVerifyCode: String) {}

    override fun changePassword(newPassword: String, newVerifyCode: String) {
        agConnectAuth.currentUser.updatePassword(
            newPassword,
            newVerifyCode,
            AGConnectAuthCredential.Email_Provider
        )
            .addOnSuccessListener {
                // onSuccess
            }.addOnFailureListener {
                // onFail
            }
    }

    override fun resetPassword(
        credential: HuaweiAuthCredential,
        newPassword: String,
        verifyCode: String
    ) {
        agConnectAuth.resetPassword(credential.email, newPassword, verifyCode)
            .addOnSuccessListener {
                // onSuccess
            }.addOnFailureListener {
                // onFail
            }
    }

    override fun getCurrentUser(): HuaweiCredentialResult? {
        val superuser = super.getCurrentUser()
        superuser?.provider = HuaweiAuthProvider.EMAIL
        return superuser
    }

}